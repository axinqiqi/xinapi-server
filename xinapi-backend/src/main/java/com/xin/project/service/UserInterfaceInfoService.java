package com.xin.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xin.project.model.dto.userinterfaceinfo.UpdateUserInterfaceInfoDTO;
import com.xin.project.model.vo.InterfaceInfoTotalVO;
import com.xin.xinapicommon.model.entity.UserInterfaceInfo;

import java.util.List;

/**
 * 用户接口信息服务
 *
 * @author youshixin
 * @description
 * @data 2023/8/18 16:24
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {


    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);

    /**
     * 更新用户接口信息
     * @param updateUserInterfaceInfoDTO
     * @return
     */
    boolean updateUserInterfaceInfo(UpdateUserInterfaceInfoDTO updateUserInterfaceInfoDTO);

    List<InterfaceInfoTotalVO> listTopInterfaceInfo(Integer num);

}

