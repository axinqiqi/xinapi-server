package com.xin.xinapicommon.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderVO implements Serializable {

    /**
     * 接口id
     */
    @ApiModelProperty(value = "接口id")
    private Long interfaceId;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNumber;

    /**
     * 接口名
     */
    @ApiModelProperty(value = "接口名")
    private String interfaceName;

    /**
     * 接口描述
     */
    @ApiModelProperty(value = "接口描述")
    private String interfaceDesc;

    /**
     * 购买数量
     */
    @ApiModelProperty(value = "购买数量")
    private Long total;

    /**
     * 单价
     */
    @ApiModelProperty(value = "单价")
    private Double charging;

    /**
     * 交易金额
     */
    @ApiModelProperty(value = "交易金额")
    private BigDecimal totalAmount;

    /**
     * 交易状态【0->待付款；1->已完成；2->无效订单】
     */
    @ApiModelProperty(value = "交易状态【0->待付款；1->已完成；2->无效订单】")
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /**
     * 过期时间
     */
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expirationTime;

    private static final long serialVersionUID = 1L;

}
