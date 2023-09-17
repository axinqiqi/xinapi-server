package com.xin.xinapicommon.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xin.xinapicommon.constant.DateConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息
 */
@Data
@TableName(value ="interface_info")
@ApiModel(value = "interface_info对象", description = "接口信息表")
public class InterfaceInfo implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 接口地址
     */
    @ApiModelProperty(value = "接口地址")
    private String url;

    /**
     * 请求参数
     */
    @ApiModelProperty(value = "请求参数")
    private String requestParams;

    /**
     * 请求头
     */
    @ApiModelProperty(value = "请求头")
    private String requestHeader;

    /**
     * 响应头
     */
    @ApiModelProperty(value = "响应头")
    private String responseHeader;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    @ApiModelProperty(value = "接口状态（0-关闭，1-开启）")
    private Integer status;

    /**
     * 请求类型
     */
    @ApiModelProperty(value = "请求类型")
    private String method;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private Long userId;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = DateConstants.DATE_TIME_PATTERN, timezone = DateConstants.TIME_ZONE_GMT8)
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = DateConstants.DATE_TIME_PATTERN, timezone = DateConstants.TIME_ZONE_GMT8)
    private Date updateTime;

    /**
     * 是否删除(0-未删, 1-已删)
     */
    @TableLogic
    @ApiModelProperty(value = "是否删除(0-未删, 1-已删)")
    private Integer isDelete;

    /**
     * 接口对应的SDK类路径
     */
    @ApiModelProperty(value = "接口对应的SDK类路径")
    private String sdk;

    /**
     * 参数示例
     */
    @ApiModelProperty(value = "参数示例")
    private String parameterExample;

    @ApiModelProperty(value = "是否免费(0-收费, 1-免费)")
    private Integer isFree;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}