package com.xin.xinapicommon.service;

import com.xin.xinapicommon.model.entity.UserInterfaceInfo;
/**
* @author 86166
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2023-08-06 21:00:23
*/
public interface InnerUserInterfaceInfoService {

    /**
     * 统计接口调用次数
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    boolean invokeCount(long userId, long interfaceInfoId);

    /**
     * 获取UserInterfaceInfo详情
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    UserInterfaceInfo getUserInterfaceInfo(long userId, long interfaceInfoId);
}
