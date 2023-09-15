package com.xin.project.model.vo;

import com.google.gson.Gson;
import com.xin.xinapicommon.model.entity.InterfaceInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口信息视图
 *
 */
@Data
public class InterfaceInfoVO extends InterfaceInfo implements Serializable {

    private final static Gson GSON = new Gson();

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
     * 本人剩余可调用次数
     */
    private String availablePieces;

    /**
     * 接口剩余可调用次数
     */
    private Integer totalAvailablePieces;

}
