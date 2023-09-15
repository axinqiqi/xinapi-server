package com.xin.project.model.dto.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderAddRequest {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 接口id
     */
    @ApiModelProperty(value = "接口id")
    private Long interfaceId;

    /**
     * 计费Id
     */
    @ApiModelProperty(value = "计费Id")
    private Long chargingId;

    /**
     * 单价
     */
    @ApiModelProperty(value = "单价")
    private Double charging;

    /**
     * 购买数量
     */
    @ApiModelProperty(value = "购买数量")
    private Integer count;

    /**
     * 订单应付价格
     */
    @ApiModelProperty(value = "订单应付价格")
    private BigDecimal totalAmount;

}
