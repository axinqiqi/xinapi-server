package com.xin.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xin.project.common.ErrorCode;
import com.xin.project.exception.BusinessException;
import com.xin.project.mapper.InterfaceInfoMapper;
import com.xin.project.mapper.UserInterfaceInfoMapper;
import com.xin.project.model.dto.userinterfaceinfo.UpdateUserInterfaceInfoDTO;
import com.xin.project.model.dto.userinterfaceinfo.UserInterfaceInfoUpdateRequest;
import com.xin.project.model.vo.InterfaceInfoTotalVO;
import com.xin.project.model.vo.UserInterfaceInfoAnalysisVo;
import com.xin.project.service.UserInterfaceInfoService;
import com.xin.xinapicommon.model.entity.InterfaceInfo;
import com.xin.xinapicommon.model.entity.UserInterfaceInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户接口信息服务实现类
 *
 * @author youshixin
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
 * @createDate 2023-08-06 21:00:23
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = userInterfaceInfo.getUserId();
        Long interfaceInfoId = userInterfaceInfo.getInterfaceInfoId();
        Integer leftNum = userInterfaceInfo.getLeftNum();
        // 创建时，参数不能为空
        if (add) {
            if (userId <= 0 || interfaceInfoId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
            QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", userId);
            queryWrapper.eq("interfaceInfoId", interfaceInfoId);
            UserInterfaceInfo one = this.getOne(queryWrapper);
            if (ObjectUtil.isNotNull(one)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户调用接口已存在");
            }
        }
        if (leftNum <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余调用次数不能小于 0");
        }
    }

    @Transactional
    @Override
    public boolean invokeCount(long userId, long interfaceInfoId) {
        if (userId <= 0 || interfaceInfoId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        UserInterfaceInfo userInterfaceInfo = this.getOne(queryWrapper);
        Integer oldLeftNum = userInterfaceInfo.getLeftNum();
        Integer oldTotalNum = userInterfaceInfo.getTotalNum();

        userInterfaceInfo.setLeftNum(oldLeftNum - 1);
        userInterfaceInfo.setTotalNum(oldTotalNum + 1);
        return this.updateById(userInterfaceInfo);
    }

    @Override
    public boolean updateUserInterfaceInfo(UpdateUserInterfaceInfoDTO updateUserInterfaceInfoDTO) {
        long userId = updateUserInterfaceInfoDTO.getUserId();
        long interfaceInfoId = updateUserInterfaceInfoDTO.getInterfaceId();
        long leftNum = updateUserInterfaceInfoDTO.getLockNum();
        if (userId <= 0 || interfaceInfoId <= 0 || leftNum <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo one = this.getOne(new QueryWrapper<UserInterfaceInfo>()
                .eq("userId", userId)
                .eq("interfaceInfoId", interfaceInfoId)
        );
        // 第一次购买
        if (ObjectUtil.isNull(one)) {
            UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
            userInterfaceInfo.setUserId(userId);
            userInterfaceInfo.setInterfaceInfoId(interfaceInfoId);
            userInterfaceInfo.setLeftNum(Math.toIntExact(leftNum));
            return this.save(userInterfaceInfo);
        } else {
            // 说明是增加数量
            return this.update(one, new UpdateWrapper<UserInterfaceInfo>()
                    .setSql("leftNum = leftNum + " + leftNum)
            );
        }
    }

    @Override
    public List<InterfaceInfoTotalVO> listTopInterfaceInfo(Integer num) {
        List<UserInterfaceInfoAnalysisVo> interfaceInfoVoList = getBaseMapper().listTopInterfaceInfo(num);
        if (CollectionUtils.isEmpty(interfaceInfoVoList)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        List<Long> interfaceInfoIds = interfaceInfoVoList.stream()
                .map(UserInterfaceInfo::getInterfaceInfoId).collect(Collectors.toList());
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", interfaceInfoIds);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoMapper.selectList(queryWrapper);

        List<InterfaceInfoTotalVO> infoVoList = new ArrayList<>(interfaceInfoList.size());
        for (int i = 0; i < interfaceInfoList.size(); i++) {
            InterfaceInfo interfaceInfo = interfaceInfoList.get(i);
            UserInterfaceInfoAnalysisVo userInterfaceInfoTotalVO = interfaceInfoVoList.get(i);
            InterfaceInfoTotalVO interfaceInfoVo = new InterfaceInfoTotalVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVo);
            interfaceInfoVo.setTotalNum(userInterfaceInfoTotalVO.getSumNum());
            infoVoList.add(interfaceInfoVo);
        }
        return infoVoList;
    }

}




