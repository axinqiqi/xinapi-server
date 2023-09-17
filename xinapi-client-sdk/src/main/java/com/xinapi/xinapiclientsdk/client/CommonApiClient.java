package com.xinapi.xinapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import com.xinapi.xinapiclientsdk.utils.SignUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 公共的API-SDK，抽取ak,sk并且封装生成签名的过程
 * @author youshixin
 */
public class CommonApiClient {

    protected final String accessKey;
    protected final String secretKey;

    protected static final String GATEWAY_HOST ="http://localhost:8121";

    public CommonApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * 负责将数字签名的相关参数填入请求头中
     * @param body body
     * @param accessKey accessKey
     * @param secretKey secretKey
     * @return map
     */
    protected Map<String, String> addHeaders(String body, String accessKey, String secretKey){
        Map<String, String> headers = new HashMap<>(8);
        headers.put("accessKey", accessKey);
        //一定不能放到请求头中
//        headers.put("secretKey", secretKey);
        headers.put("body", body);
        headers.put("sign", SignUtils.getSign(body, secretKey));
        headers.put("nonce", RandomUtil.randomNumbers(4));
        headers.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        return headers;
    }


}
