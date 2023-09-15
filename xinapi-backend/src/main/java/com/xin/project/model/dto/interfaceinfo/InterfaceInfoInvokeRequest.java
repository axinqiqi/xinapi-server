package com.xin.project.model.dto.interfaceinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 模拟接口调用请求
 *
 * @author youshixin
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 请求参数
     */
    @ApiModelProperty(value = "请求参数")
    private String userRequestParams;


    private static final long serialVersionUID = 1L;
}