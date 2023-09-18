package com.xin.project.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.google.gson.Gson;
import com.xin.project.common.BaseResponse;
import com.xin.project.common.DeleteRequest;
import com.xin.project.common.ErrorCode;
import com.xin.project.common.ResultUtils;
import com.xin.project.constant.RedisConstant;
import com.xin.project.constant.UserConstant;
import com.xin.project.exception.BusinessException;
import com.xin.project.exception.ThrowUtils;
import com.xin.project.model.dto.user.*;
import com.xin.project.model.vo.UserVO;
import com.xin.project.service.UserService;
import com.xin.project.utils.JwtUtils;
import com.xin.xinapicommon.model.entity.User;
import com.xin.xinapicommon.model.enums.UserRoleEnum;
import com.xin.xinapicommon.model.vo.LoginUserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用户接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/user")
@Api(tags = "1-用户接口")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private Gson gson;

    // region 登录相关

    /**
     * 用户注册（账号密码注册）
     *
     * @param userRegisterRequest
     * @return
     */
    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest, HttpServletRequest request) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 验证码
        String signature = request.getHeader("signature");
        if (StrUtil.isBlank(signature)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String captcha = userRegisterRequest.getCaptcha();
        String redisCaptcha = stringRedisTemplate.opsForValue().get(RedisConstant.CAPTCHA_PREFIX + signature);
        if (!captcha.equals(redisCaptcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码不正确");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户注册（qq邮箱注册）
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/email/register")
    public BaseResponse<Long> userEmailRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String emailCaptcha = userRegisterRequest.getEmailCaptcha();
        String emailNum = userRegisterRequest.getEmailNum();
        if (StringUtils.isAnyBlank(emailNum, emailCaptcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱或邮箱验证码不能为空");
        }

        long result = userService.userEmailRegister(emailNum, emailCaptcha);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request, HttpServletResponse response) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request, response);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * qq邮箱登录
     * @param userLoginRequest
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/loginBySms")
    public BaseResponse<LoginUserVO> userLoginBySms(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request, HttpServletResponse response) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String emailNum = userLoginRequest.getEmailNum();
        String emailCaptcha = userLoginRequest.getEmailCaptcha();
        if (StringUtils.isAnyBlank(emailNum, emailCaptcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        LoginUserVO user = userService.userLoginBySms(emailNum, emailCaptcha, request, response);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "用户注销")
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request, HttpServletResponse response) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request, response);
        return ResultUtils.success(result);
    }

    // [加入编程导航](https://yupi.icu) 深耕编程提升【两年半】、国内净值【最高】的编程社群、用心服务【20000+】求学者、帮你自学编程【不走弯路】

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "获取当前登录用户")
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return ResultUtils.success(loginUserVO);
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "创建用户")
    @PostMapping("/add")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "删除用户")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "更新用户")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 允许用户修改自己的信息，但拒绝用户修改别人的信息；但管理员可以修改别人的信息
        User loginUser = userService.getLoginUser(request);
        Long id = userUpdateRequest.getId();
        if (!loginUser.getId().equals(id)) {
            if (!loginUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        //修改完要更新用户缓存
        loginUser.setUserName(userUpdateRequest.getUserName());
        loginUser.setGender(userUpdateRequest.getGender());
        String userJson = gson.toJson(loginUser);
        stringRedisTemplate.opsForValue().set(UserConstant.USER_LOGIN_STATE + loginUser.getId(), userJson, JwtUtils.EXPIRE, TimeUnit.MILLISECONDS);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户
     *
     * @param id
     * @param request
     * @return
     */
    @ApiOperation(value = "根据 id 获取用户")
    @GetMapping("/get")
    public BaseResponse<UserVO> getUserById(int id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "获取用户列表")
    @GetMapping("/list")
    public BaseResponse<List<UserVO>> listUser(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        List<User> userList = userService.list(queryWrapper);
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    /**
     * 分页获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "分页获取用户列表")
    @GetMapping("/list/page")
    public BaseResponse<Page<UserVO>> listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        long current = 1;
        long size = 10;
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
            current = userQueryRequest.getCurrent();
            size = userQueryRequest.getPageSize();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        Page<User> userPage = userService.page(new Page<>(current, size), queryWrapper);
        Page<UserVO> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }

    /**
     * 发送邮箱验证码(备案后会改为发送手机短信验证码)
     *
     * @param emailNum
     * @return
     */
    @GetMapping("/smsCaptcha")
    public BaseResponse<String> sendCode(@RequestParam String emailNum, @RequestParam String captchaType) {
        if (StringUtils.isBlank(emailNum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱为空！");
        }
        //^1[3-9]\d{9}$ 手机号正则表达式
        //^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$    邮箱正则表达式
        if (!Pattern.matches("[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", emailNum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误!");
        }

        userService.sendCode(emailNum, captchaType);
        return ResultUtils.success("验证码已发送");
    }

    /**
     * 获取图形验证码
     *
     * @param request
     * @param response
     */
    @GetMapping("/getCaptcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        userService.getCaptcha(request, response);
    }

    /**
     * 获取accessKey、secretKey
     *
     * @param request
     * @return
     */
    @GetMapping("/key")
    public BaseResponse<User> getKey(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", loginUser.getId());
        queryWrapper.select("accessKey", "secretKey");
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(user);
    }

    @PostMapping("/gen/key")
    public BaseResponse<User> refreshKey(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        User user = userService.refreshKey(loginUser, request);
        return ResultUtils.success(user);
    }

}

