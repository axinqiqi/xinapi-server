package com.xin.xinapicommon.service;

import com.xin.xinapicommon.model.entity.InterfaceInfo;

/**
 * @author Administrator
 * 针对表【interface_info(接口信息)】的数据库操作Service
 * 2023-06-15 17:15:40
 */
public interface InnerInterfaceInfoService {

    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法）
     *
     * @param url url
     * @param method method
     * @return InterfaceInfo
     */
    InterfaceInfo getInterfaceInfo(String url, String method);

}
