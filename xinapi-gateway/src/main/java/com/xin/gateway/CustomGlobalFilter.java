package com.xin.gateway;

import cn.hutool.core.util.ObjectUtil;
import com.xin.common.ErrorCode;
import com.xin.exception.BusinessException;
import com.xin.xinapicommon.model.entity.InterfaceInfo;
import com.xin.xinapicommon.model.entity.User;
import com.xin.xinapicommon.model.entity.UserInterfaceInfo;
import com.xin.xinapicommon.service.InnerInterfaceInfoService;
import com.xin.xinapicommon.service.InnerUserInterfaceInfoService;
import com.xin.xinapicommon.service.InnerUserService;
import com.xinapi.xinapiclientsdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 全局过滤
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    private static final List<String> IP_WHITE_LIST = Collections.singletonList("127.0.0.1");

    public static final String INTERFACE_HOST = "localhost:8111";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.请求日志
        ServerHttpRequest request = exchange.getRequest();
        String path = INTERFACE_HOST + request.getPath().value();
        String method = request.getMethod().toString();
        log.info("请求唯一标识: " + request.getId());
        log.info("请求路径: " + path);
        log.info("请求方法: " + method);
        log.info("请求参数: " + request.getQueryParams());
        String sourceAddress = request.getRemoteAddress().getHostString();
        log.info("请求来源地址: " + sourceAddress);
        log.info("请求来源地址: " + request.getLocalAddress());
        // 2.黑白名单
        ServerHttpResponse response = exchange.getResponse();
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            return handleNoAuth(response);
        }
        // 3.用户鉴权（判断ak，sk是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String body = headers.getFirst("body");
        if (body != null) {
            try {
                body = URLDecoder.decode(body, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("unexpected inability to UTF-8 decode", e);
            }
        }
        String sign = headers.getFirst("sign");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        // 去数据库查找是否已分配给用户
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error", e);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (invokeUser == null) {
            return handleNoAuth(response);
        }
        // 时间和当前时间不能超过5分钟
        long currentTimeMillis = System.currentTimeMillis() / 1000;
        long fiveMinutes = 5L * 60;
        if (currentTimeMillis - Long.parseLong(timestamp) > fiveMinutes) {
            return handleNoAuth(response);
        }
        String secretKey = invokeUser.getSecretKey();
        String secretSign = SignUtils.getSign(body, secretKey);
        if (sign == null || !sign.equals(secretSign)) {
            return handleNoAuth(response);
        }
        // 4.请求的模拟接口是否存在？以及请求方法是否匹配
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            log.error("getInterfaceInfo error", e);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }
        // 5.请求转发，调用模拟接口， 响应日志
        return handleResponse(exchange, chain, invokeUser.getId(), interfaceInfo.getId());

    }

    private Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    private Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

    /**
     * 装饰 response，解决异步返回问题
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long userId, long interfaceInfoId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();

            HttpStatus statusCode = originalResponse.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        //log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                // 6.调用成功，接口调用次数 + 1
                                boolean invokeCount = false;
                                try {
                                    invokeCount = innerUserInterfaceInfoService.invokeCount(userId, interfaceInfoId);
                                } catch (Exception e) {
                                    log.error("invokeCount error", e);
                                }
                                if (!invokeCount) {
                                    throw new BusinessException(ErrorCode.OPERATION_ERROR);
                                }

                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                StringBuilder sb2 = new StringBuilder(200);
                                sb2.append("<--- {} {} \n");
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                //rspArgs.add(requestUrl);
                                String data = new String(content, StandardCharsets.UTF_8);//data

                                // 7.调用失败，返回一个规范的错误码
//                                log.info("custom global filter");
                                sb2.append(data);
                                log.info("响应结果： {}", data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);//降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常： " + e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}