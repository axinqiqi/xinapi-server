package com.xin.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xin.xinapicommon.model.entity.InterfaceCharging;

/**
 * @author Administrator
 * @description 针对表【interface_charging】的数据库操作Service
 * @createDate 2023-08-25 15:15:55
 */
public interface InterfaceChargingService extends IService<InterfaceCharging> {

    /**
     * 订单支付超时，回滚库存
     *
     * @param interfaceId
     * @param num
     * @return
     */
    boolean recoverInterfaceStock(long interfaceId, Integer num);

}
