package com.xin.project.common;

import com.xin.project.constant.CommonConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    @ApiModelProperty(value = "当前页号")
    private long current = 1;

    /**
     * 页面大小
     */
    @ApiModelProperty(value = "页面大小")
    private long pageSize = 10;

    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段")
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    @ApiModelProperty(value = "排序顺序（默认升序）")
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
}
