package com.xin.xinapicommon.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xin.xinapicommon.constant.DateConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * interface_charging
 */
@Data
@TableName(value ="interface_charging")
@ApiModel(value = "interface_charging对象", description = "接口计费表")
public class InterfaceCharging implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接口id
     */
    @ApiModelProperty(value = "接口id")
    private Long interfaceId;

    /**
     * 计费规则（元/条）
     */
    @ApiModelProperty(value = "计费规则（元/条）")
    private Double charging;

    /**
     * 接口剩余可调用次数
     */
    @ApiModelProperty(value = "接口剩余可调用次数")
    private Integer availablePieces;

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
     * 是否删除(0-删除 1-正常)
     */
    @ApiModelProperty(value = "是否删除(0-删除 1-正常)")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}