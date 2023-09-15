package com.xin.xinapicommon.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单表
 *
 * @TableName t_order
 */
@Data
@TableName(value = "t_order")
@ApiModel(value = "t_order对象", description = "订单表")
public class Order implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableLogic
    @ApiModelProperty(value = "是否删除(0-未删, 1-已删)")
    private Integer isDelete;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "接口id")
    private Long interfaceId;

    @ApiModelProperty(value = "购买数量")
    private Integer count;

    @ApiModelProperty(value = "订单应付价格")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "订单状态 0-未支付 1 -已支付 2-超时支付")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "订单号")
    private String orderSn;

    @ApiModelProperty(value = "单价")
    private Double charging;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}