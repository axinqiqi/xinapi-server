package com.xin.project.model.dto.userinterfaceinfo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 编辑请求
 *
 */
@Data
public class UserInterfaceInfoEditRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

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
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 是否删除(0-未删, 1-已删)
     */
    @ApiModelProperty(value = "是否删除(0-未删, 1-已删)")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}