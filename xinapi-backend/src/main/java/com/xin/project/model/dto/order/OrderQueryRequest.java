package com.xin.project.model.dto.order;


import com.xin.project.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderQueryRequest extends PageRequest implements Serializable {

    @ApiModelProperty(value = "订单状态 0-未支付 1 -已支付 2-超时支付")
    private String type;

}
