package com.xin.project.model.dto.ali;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author youshixin
 * @create 2023-08-29 19:06
 */
@Data
public class AlipayRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单号")
    private String traceNo;

    @ApiModelProperty(value = "订单总金额")
    private double totalAmount;

    @ApiModelProperty(value = "订单标题")
    private String subject;

}
