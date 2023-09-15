package com.xin.project.listener;

import com.rabbitmq.client.Channel;
import com.xin.project.constant.RabbitmqConstant;
import com.xin.project.utils.SendMessageOperation;
import com.xin.xinapicommon.model.entity.SmsMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;


@Component
@Slf4j
public class MessageListener {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    //监听queue_sms_code队列，实现接口统计功能
    //生产者是懒加载机制，消费者是饿汉加载机制，二者机制不对应，所以消费者要自行创建队列并加载，否则会报错
    @RabbitListener(queuesToDeclare = {@Queue(RabbitmqConstant.QUEUE_LOGIN_SMS)})
    public void receiveSms(SmsMessage smsMessage, Message message, Channel channel) throws IOException {
        log.info("监听到消息啦，内容是：" + smsMessage);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        //发送邮箱验证码
        SendMessageOperation messageOperation = new SendMessageOperation();
        String targetEmail = smsMessage.getEmail();
        String code = smsMessage.getCode();
        messageOperation.sendMessage(targetEmail, code, stringRedisTemplate);

    }


}