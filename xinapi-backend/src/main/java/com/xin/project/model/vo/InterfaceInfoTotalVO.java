package com.xin.project.model.vo;

import com.xin.xinapicommon.model.entity.InterfaceInfo;
import lombok.Data;

/**
 * @author youshixin
 * @description
 * @data 2023/8/31 18:29
 */
@Data
public class InterfaceInfoTotalVO extends InterfaceInfo {

    /**
     * 统计每个接口被用户调用的总数
     */
    private Integer totalNum;


    /**
     * 计费规则（元/条）
     */
    private Double charging;

    /**
     * 计费Id
     */
    private Long chargingId;

    /**
     * 接口剩余可调用次数
     */
    private String availablePieces;

}
