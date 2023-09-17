package com.xin.xinapicommon.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单vo
 * @author youshixin
 */
@Data
public class OrderVO implements Serializable {

    @ApiModelProperty(value = "接口id")
    private Long interfaceId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "订单号")
    private String orderNumber;

    @ApiModelProperty(value = "接口名")
    private String interfaceName;

    @ApiModelProperty(value = "接口描述")
    private String interfaceDesc;

    @ApiModelProperty(value = "购买数量")
    private Long total;

    @ApiModelProperty(value = "单价")
    private Double charging;

    @ApiModelProperty(value = "交易金额")
    private BigDecimal totalAmount;

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
