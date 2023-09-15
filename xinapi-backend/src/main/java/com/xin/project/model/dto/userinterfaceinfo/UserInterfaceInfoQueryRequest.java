package com.xin.project.model.dto.userinterfaceinfo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xin.project.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInterfaceInfoQueryRequest extends PageRequest implements Serializable {
    /**
     * 主键
     */
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
    private Date createTime;

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