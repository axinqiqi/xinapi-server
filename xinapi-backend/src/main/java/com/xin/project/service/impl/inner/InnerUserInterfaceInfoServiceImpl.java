package com.xin.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xin.project.mapper.UserInterfaceInfoMapper;
import com.xin.project.service.UserInterfaceInfoService;
import com.xin.xinapicommon.model.entity.UserInterfaceInfo;
import com.xin.xinapicommon.service.InnerUserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author youshixin
 * @description
 * @data 2023/8/18 17:11
 */
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Override
    public boolean invokeCount(long userId, long interfaceInfoId) {
        return userInterfaceInfoService.invokeCount(userId, interfaceInfoId);
    }

    @Override
    public UserInterfaceInfo getUserInterfaceInfo(long userId, long interfaceInfoId) {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        return userInterfaceInfoMapper.selectOne(queryWrapper);
    }
}
