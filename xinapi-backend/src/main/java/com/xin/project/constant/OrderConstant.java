package com.xin.project.constant;

public interface OrderConstant {

    Integer ORDER_PAY_SUCCESS_STATUS = 1;

    //消息队列发送端的可靠性机制，保障发送端的消息成功路由到队列中
    String CONSUME_ORDER_PAY_SUCCESS_INFO = "rabbitmq:consume:order:paySuccess:message:";

    Integer ORDER_TIMEOUT_STATUS = 2;

    Integer ORDER_UNPAY_STATUS = 0;
}
