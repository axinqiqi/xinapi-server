package com.xin.project.utils;

import com.xin.project.constant.RabbitmqConstant;
import com.xin.project.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

/**
 *
 */
@Component
@Slf4j
public class OrderPaySuccessMqUtils {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;


    /**
     * @param outTradeNo 我们自己的订单号
     */
    public void sendOrderPaySuccess(String outTradeNo) {

        redisTemplate.opsForValue().set(RedisConstant.SEND_ORDER_PAY_SUCCESS_INFO + outTradeNo, outTradeNo);
        String finalMessageId = UUID.randomUUID().toString();
        rabbitTemplate.convertAndSend(RabbitmqConstant.ORDER_EXCHANGE_NAME, RabbitmqConstant.ORDER_SUCCESS_EXCHANGE_ROUTING_KEY, outTradeNo, message -> {
            MessageProperties messageProperties = message.getMessageProperties();
            //生成全局唯一id
            messageProperties.setMessageId(finalMessageId);
            messageProperties.setContentEncoding("utf-8");
            return message;
        });
        log.info("消息队列给订单服务发送支付成功消息，订单好：" + outTradeNo);
    }

}
