package com.xin.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xin.xinapicommon.model.entity.InterfaceInfo;

/**
 * 接口信息服务
 *
 * @author youshixin
 * @description
 * @data 2023/8/18 16:23
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}

