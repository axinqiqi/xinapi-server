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
 * 用户调用接口关系
 */
@Data
@TableName(value ="user_interface_info")
@ApiModel(value = "user_interface_info对象", description = "用户调用接口关系表")
public class UserInterfaceInfo implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 调用用户 id
     */
    @ApiModelProperty(value = "调用用户 id")
    private Long userId;

    /**
     * 接口 id
     */
    @ApiModelProperty(value = "接口 id")
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    @ApiModelProperty(value = "总调用次数")
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    @ApiModelProperty(value = "剩余调用次数")
    private Integer leftNum;

    /**
     * 0-正常，1-禁用
     */
    @ApiModelProperty(value = "0-正常，1-禁用")
    private Integer status;

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

    @Version
    @ApiModelProperty(value = "版本号")
    private Long version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}