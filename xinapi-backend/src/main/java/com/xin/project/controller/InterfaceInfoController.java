package com.xin.project.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.xin.project.annotation.AuthCheck;
import com.xin.project.common.*;
import com.xin.project.constant.CommonConstant;
import com.xin.project.constant.UserConstant;
import com.xin.project.exception.BusinessException;
import com.xin.project.exception.ThrowUtils;
import com.xin.project.model.dto.interfaceinfo.*;
import com.xin.project.model.enums.InterfaceInfoStatusEnum;
import com.xin.project.model.vo.InterfaceInfoVO;
import com.xin.project.service.InterfaceChargingService;
import com.xin.project.service.InterfaceInfoService;
import com.xin.project.service.UserInterfaceInfoService;
import com.xin.project.service.UserService;
import com.xin.xinapicommon.model.entity.InterfaceCharging;
import com.xin.xinapicommon.model.entity.InterfaceInfo;
import com.xin.xinapicommon.model.entity.User;
import com.xin.xinapicommon.model.entity.UserInterfaceInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 接口信息
 */
@Api(tags = "2-接口信息接口")
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private InterfaceChargingService interfaceChargingService;

    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "创建")
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);

        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
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
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param interfaceInfoUpdateRequest
     * @return
     */
    @ApiOperation(value = "更新（仅管理员）")
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = interfaceInfoService.updateById(interfaceInfo);
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
    public BaseResponse<InterfaceInfoVO> getInterfaceInfoById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        InterfaceCharging interfaceCharging = interfaceChargingService.getOne(new QueryWrapper<InterfaceCharging>().eq("interfaceId", id));
        InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
        BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
        if (interfaceCharging != null) {
            // 获取付费剩余调用次数
            interfaceInfoVO.setCharging(interfaceCharging.getCharging());
            interfaceInfoVO.setChargingId(interfaceCharging.getId());
            interfaceInfoVO.setTotalAvailablePieces(interfaceCharging.getAvailablePieces());
        }
        // 获取剩余调用次数
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        QueryWrapper<UserInterfaceInfo> userInterfaceInfoQueryWrapper = new QueryWrapper<>();
        userInterfaceInfoQueryWrapper.eq("userId", userId);
        userInterfaceInfoQueryWrapper.eq("interfaceInfoId", id);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(userInterfaceInfoQueryWrapper);
        if (userInterfaceInfo != null) {
            interfaceInfoVO.setAvailablePieces(userInterfaceInfo.getLeftNum().toString());
        }

        return ResultUtils.success(interfaceInfoVO);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "分页获取列表")
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // description 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

    /**
     * 编辑（用户）
     *
     * @param interfaceInfoEditRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑（用户）")
    @PostMapping("/edit")
    public BaseResponse<Boolean> editInterfaceInfo(@RequestBody InterfaceInfoEditRequest interfaceInfoEditRequest, HttpServletRequest request) {
        if (interfaceInfoEditRequest == null || interfaceInfoEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoEditRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        User loginUser = userService.getLoginUser(request);
        long id = interfaceInfoEditRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldInterfaceInfo.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 接口上线
     *
     * @param idRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "接口上线")
    @PostMapping("/online")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断该接口是否存在
        long id = idRequest.getId();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        //判断该接口是否可以调用
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        Object res = invokeInterfaceInfo(oldInterfaceInfo.getSdk(), oldInterfaceInfo.getName(), oldInterfaceInfo.getParameterExample(), accessKey, secretKey);
        if (res == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (res.toString().contains("Error request")) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统接口内部异常");
        }
        //修改数据库状态为1
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 接口下线
     *
     * @param idRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "接口下线")
    @PostMapping("/offline")
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断该接口是否存在
        long id = idRequest.getId();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        //修改数据库状态为0
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 模拟接口调用
     *
     * @param interfaceInfoInvokeRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "模拟接口调用")
    @PostMapping("/invoke")
    public BaseResponse invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request) {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断该接口是否存在
        long id = interfaceInfoInvokeRequest.getId();
//        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
//        ThrowUtils.throwIf(userRequestParams == null, ErrorCode.PARAMS_ERROR, "请输入请求参数");
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(interfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        if (interfaceInfo.getStatus() != InterfaceInfoStatusEnum.ONLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口未开启");
        }
        User loginUser = userService.getLoginUser(request);
        // 是否还有调用次数
        if (interfaceInfo.getIsFree() == 0) {
            QueryWrapper<UserInterfaceInfo> userInterfaceInfoQueryWrapper = new QueryWrapper<>();
            userInterfaceInfoQueryWrapper.eq("userId", loginUser.getId());
            userInterfaceInfoQueryWrapper.eq("interfaceInfoId", interfaceInfo.getId());
            UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(userInterfaceInfoQueryWrapper);
            if (ObjectUtil.isNull(userInterfaceInfo)) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
            int leftNum = userInterfaceInfo.getLeftNum();
            if (leftNum < 1) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "剩余调用次数不足");
            }
        }

        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        // 发起接口调用
        String requestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        Object res = invokeInterfaceInfo(interfaceInfo.getSdk(), interfaceInfo.getName(), requestParams, accessKey, secretKey);
        if (res == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (res.toString().contains("Error request")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用错误，请检查参数和接口调用次数！");
        }
        Map<String, Object> resultMap = JSONUtil.parseObj(res);
        if (ObjectUtil.isNotNull(resultMap.get("code")) && "0".equals(resultMap.get("code").toString())) {
            return ResultUtils.success(resultMap.get("data"));
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR);
    }

    private Object invokeInterfaceInfo(String classPath, String methodName, String userRequestParams,
                                       String accessKey, String secretKey) {
        try {
            Class<?> clientClazz = Class.forName(classPath);
            // 1. 获取构造器，参数为ak,sk
            Constructor<?> binApiClientConstructor = clientClazz.getConstructor(String.class, String.class);
            // 2. 构造出客户端
            Object apiClient = binApiClientConstructor.newInstance(accessKey, secretKey);

            // 3. 找到要调用的方法
            Method[] methods = clientClazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    // 3.1 获取参数类型列表
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 0) {
                        // 如果没有参数，直接调用
                        return method.invoke(apiClient);
                    }
                    Gson gson = new Gson();
                    // 构造参数
                    Object parameter = gson.fromJson(userRequestParams, parameterTypes[0]);
                    return method.invoke(apiClient, parameter);
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "找不到调用的方法!! 请检查请求参数是否正确!");
        }
    }

    /**
     * 下载sdk
     * @param response
     * @throws IOException
     */
    @GetMapping("/sdk")
    public void getSdk(HttpServletResponse response) throws IOException {
        // 获取要下载的文件
        org.springframework.core.io.Resource resource = new ClassPathResource("jar/xinapi-client-sdk-0.0.1.jar");
        InputStream inputStream = resource.getInputStream();

        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=xinapi-client-sdk-0.0.1.jar");

        // 将文件内容写入响应
        try (OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
    }
}
