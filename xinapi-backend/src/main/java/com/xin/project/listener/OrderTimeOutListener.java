package com.xin.project.listener;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.rabbitmq.client.Channel;
import com.xin.project.config.OrderRabbitmqConfig;
import com.xin.project.constant.OrderConstant;
import com.xin.project.service.InterfaceChargingService;
import com.xin.project.service.OrderService;
import com.xin.xinapicommon.model.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
@Slf4j
public class OrderTimeOutListener {

    @Resource
    private InterfaceChargingService interfaceChargingService;

    @Resource
    private OrderService orderService;

    // 监听queue_order_dlx_queue死信队列，实现支付超时的回滚功能
    // 生产者是懒加载机制，消费者是饿汉加载机制，二者机制不对应，所以消费者要自行创建队列并加载，否则会报错
    @RabbitListener(queuesToDeclare = {@Queue(OrderRabbitmqConfig.QUEUE_ORDER_DLX_QUEUE)})
    public void receiveOrderMsg(Order order, Message message, Channel channel) throws IOException {
        log.info("监听到消息啦，内容是：" + order.toString());
        Order dbOrder = orderService.getById(order.getId());
        //根据订单状态判断订单是否支付成功，如果没有支付成功则回滚
        if (dbOrder.getStatus().equals(OrderConstant.ORDER_UNPAY_STATUS)) {
            Long interfaceId = dbOrder.getInterfaceId();
            Integer count = order.getCount();
            try {
                boolean success = interfaceChargingService.recoverInterfaceStock(interfaceId, count);
                if (!success) {
                    log.error("回滚库存失败!!!");
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                }
                UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("status", OrderConstant.ORDER_TIMEOUT_STATUS);
                updateWrapper.eq("id", dbOrder.getId());
                orderService.update(updateWrapper);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (Exception e) {
                log.error("回滚库存失败!!!");
                e.printStackTrace();
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            }
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}