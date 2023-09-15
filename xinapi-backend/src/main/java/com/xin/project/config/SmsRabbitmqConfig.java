package com.xin.project.config;

import com.xin.project.constant.RabbitmqConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 声明异步发送短信所需要用到的交换机和队列
 */
@Configuration
@Slf4j
public class SmsRabbitmqConfig {


    //声明交换机
    @Bean(RabbitmqConstant.EXCHANGE_SMS_INFORM)
    public Exchange EXCHANGE_SMS_INFORM(){
        return new DirectExchange(RabbitmqConstant.EXCHANGE_SMS_INFORM,true,false);
    }

    //声明QUEUE_LOGIN_SMS队列
    @Bean(RabbitmqConstant.QUEUE_LOGIN_SMS)
    public Queue QUEUE_INTERFACE_SMS(){
        return new Queue(RabbitmqConstant.QUEUE_LOGIN_SMS,true,false,false);
    }

    //交换机绑定队列
    @Bean
    public Binding BINDING_QUEUE_LOGIN_SMS(){
        return new Binding(RabbitmqConstant.QUEUE_LOGIN_SMS,
                Binding.DestinationType.QUEUE, RabbitmqConstant.EXCHANGE_SMS_INFORM,
                RabbitmqConstant.ROUTINGKEY_SMS,null);
    }
}