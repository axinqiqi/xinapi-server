package com.xin.project.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.xin.project.annotation.AuthCheck;
import com.xin.project.common.BaseResponse;
import com.xin.project.common.DeleteRequest;
import com.xin.project.common.ErrorCode;
import com.xin.project.common.ResultUtils;
import com.xin.project.constant.CommonConstant;
import com.xin.project.constant.UserConstant;
import com.xin.project.exception.BusinessException;
import com.xin.project.exception.ThrowUtils;
import com.xin.project.model.dto.userinterfaceinfo.*;
import com.xin.project.service.InterfaceInfoService;
import com.xin.project.service.UserInterfaceInfoService;
import com.xin.project.service.UserService;
import com.xin.xinapicommon.model.entity.InterfaceInfo;
import com.xin.xinapicommon.model.entity.User;
import com.xin.xinapicommon.model.entity.UserInterfaceInfo;
import com.xin.xinapicommon.model.vo.UserInterfaceInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户调用接口关系
 */
@Api(tags = "3-用户调用接口关系接口")
@RestController
@RequestMapping("/userInterfaceInfo")
@Slf4j
public class UserInterfaceInfoController {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param userInterfaceInfoAddRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "创建")
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUserInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest userInterfaceInfoAddRequest, HttpServletRequest request) {
        if (userInterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoAddRequest, userInterfaceInfo);

        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        userInterfaceInfo.setUserId(loginUser.getId());
        boolean result = userInterfaceInfoService.save(userInterfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newUserInterfaceInfoId = userInterfaceInfo.getId();
        return ResultUtils.success(newUserInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "删除")
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = userInterfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param userInterfaceInfoUpdateRequest
     * @return
     */
    @ApiOperation(value = "更新（仅管理员）")
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserInterfaceInfo(@RequestBody UserInterfaceInfoUpdateRequest userInterfaceInfoUpdateRequest) {
        if (userInterfaceInfoUpdateRequest == null || userInterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoUpdateRequest, userInterfaceInfo);
        // 参数校验
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, false);
        long id = userInterfaceInfoUpdateRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据 id 获取")
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserInterfaceInfo> getUserInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getById(id);
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(userInterfaceInfo);
    }

    /**
     * 分页获取列表
     *
     * @param userInterfaceInfoQueryRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "分页获取列表")
    @GetMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserInterfaceInfo>> listUserInterfaceInfoByPage(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest, HttpServletRequest request) {
        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfoQuery = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoQueryRequest, userInterfaceInfoQuery);
        long current = userInterfaceInfoQueryRequest.getCurrent();
        long size = userInterfaceInfoQueryRequest.getPageSize();
        String sortField = userInterfaceInfoQueryRequest.getSortField();
        String sortOrder = userInterfaceInfoQueryRequest.getSortOrder();
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(userInterfaceInfoQuery);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<UserInterfaceInfo> userInterfaceInfoPage = userInterfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(userInterfaceInfoPage);
    }

    @ApiOperation(value = "获取我的接口列表")
    @GetMapping("/list/userId")
    public BaseResponse<List<UserInterfaceInfoVO>> getInterfaceInfoByUserId(@RequestParam Long userId, HttpServletRequest request) {
        if (ObjectUtil.isNull(userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 判断用户是否有权限
        if (!loginUser.getId().equals(userId) && !loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        QueryWrapper<UserInterfaceInfo> userInterfaceInfoQueryWrapper = new QueryWrapper<>();
        userInterfaceInfoQueryWrapper.eq("userId", userId);
        List<UserInterfaceInfo> list = userInterfaceInfoService.list(userInterfaceInfoQueryWrapper);

        List<UserInterfaceInfoVO> userInterfaceInfoVOList = list.stream().map(userInterfaceInfo -> {
            InterfaceInfo interfaceInfo = interfaceInfoService.getById(userInterfaceInfo.getInterfaceInfoId());
            UserInterfaceInfoVO userInterfaceInfoVO = new UserInterfaceInfoVO();
            BeanUtils.copyProperties(userInterfaceInfo, userInterfaceInfoVO);

            userInterfaceInfoVO.setName(interfaceInfo.getName());
            userInterfaceInfoVO.setDescription(interfaceInfo.getDescription());
            userInterfaceInfoVO.setUrl(interfaceInfo.getUrl());
            userInterfaceInfoVO.setInterfaceStatus(interfaceInfo.getStatus());
            userInterfaceInfoVO.setMethod(interfaceInfo.getMethod());
            return userInterfaceInfoVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userInterfaceInfoVOList);
    }

    /**
     * 编辑（用户）
     *
     * @param userInterfaceInfoEditRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑（用户）")
    @PostMapping("/edit")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> editUserInterfaceInfo(@RequestBody UserInterfaceInfoEditRequest userInterfaceInfoEditRequest, HttpServletRequest request) {
        if (userInterfaceInfoEditRequest == null || userInterfaceInfoEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoEditRequest, userInterfaceInfo);
        // 参数校验
        userInterfaceInfoService.validUserInterfaceInfo(userInterfaceInfo, false);
        User loginUser = userService.getLoginUser(request);
        long id = userInterfaceInfoEditRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldUserInterfaceInfo.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 给用户分配免费的调用次数
     * @param updateUserInterfaceInfoDTO
     * @param request
     * @return
     */
    @PostMapping("/get/free")
    public BaseResponse<Boolean> getFreeInterfaceCount(@RequestBody UpdateUserInterfaceInfoDTO updateUserInterfaceInfoDTO, HttpServletRequest request) {
        Long interfaceId = updateUserInterfaceInfoDTO.getInterfaceId();
        Long userId = updateUserInterfaceInfoDTO.getUserId();
        Long lockNum = updateUserInterfaceInfoDTO.getLockNum();
        if (interfaceId == null || userId == null || lockNum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (lockNum > 100) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "您一次性获取的次数太多了");
        }
        synchronized (this) {
            User loginUser = userService.getLoginUser(request);
            if (!userId.equals(loginUser.getId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
//            long interfaceCharging = interfaceChargingService.count(new QueryWrapper<InterfaceCharging>().eq("interfaceId", interfaceId));
//            if (interfaceCharging > 0) {
//                throw new BusinessException(ErrorCode.PARAMS_ERROR, "这个是付费接口噢!");
//            }
            UserInterfaceInfo one = userInterfaceInfoService.getOne(new QueryWrapper<UserInterfaceInfo>().eq("userId", userId).eq("interfaceInfoId", interfaceId));
            if (one != null && one.getLeftNum() >= 1000) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "您获取的次数太多了");
            }

            boolean result = userInterfaceInfoService.updateUserInterfaceInfo(updateUserInterfaceInfoDTO);
            return ResultUtils.success(result);
        }
    }

}
