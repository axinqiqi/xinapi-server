package com.xin.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xin.project.mapper.InterfaceChargingMapper;
import com.xin.project.service.InterfaceChargingService;
import com.xin.xinapicommon.model.entity.InterfaceCharging;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @description 针对表【interface_charging】的数据库操作Service实现
 * @createDate 2023-08-25 15:15:55
 */
@Service
public class InterfaceChargingServiceImpl extends ServiceImpl<InterfaceChargingMapper, InterfaceCharging>
        implements InterfaceChargingService {

    @Override
    public boolean recoverInterfaceStock(long interfaceId, Integer num) {
        UpdateWrapper<InterfaceCharging> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("availablePieces = availablePieces + " + num)
                .eq("interfaceId", interfaceId);
        return this.update(updateWrapper);
    }
}




