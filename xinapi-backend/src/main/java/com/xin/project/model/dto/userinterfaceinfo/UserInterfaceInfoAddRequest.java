package com.xin.project.model.dto.userinterfaceinfo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建请求
 *
 */
@Data
public class UserInterfaceInfoAddRequest implements Serializable {

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
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 是否删除(0-未删, 1-已删)
     */
    @TableLogic
    @ApiModelProperty(value = "是否删除(0-未删, 1-已删)")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}