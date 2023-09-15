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
 * 用户
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
@TableName(value = "user")
@ApiModel(value = "user对象", description = "用户表")
public class User implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号")
    private String userAccount;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String userAvatar;
    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    private Integer gender;

    /**
     * 用户角色：user/admin/ban
     */
    @ApiModelProperty(value = "用户角色：user/admin/ban")
    private String userRole;

    /**
     * 用户密码
     */
    @ApiModelProperty(value = "用户密码")
    private String userPassword;

    @ApiModelProperty(value = "accessKey")
    private String accessKey;

    @ApiModelProperty(value = "secretKey")
    private String secretKey;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String userName;

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
     * 是否删除
     */
    @TableLogic
    @ApiModelProperty(value = "是否删除(0-未删, 1-已删)")
    private Integer isDelete;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}