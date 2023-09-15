package com.xin.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xin.xinapicommon.model.entity.User;
import com.xin.xinapicommon.model.vo.LoginUserVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户服务
 *
 * @author youshixin
 * @description
 * @data 2023/8/18 16:25
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 使用邮箱注册(后续会改造成使用手机号注册)
     * @param emailNum
     * @param emailCaptcha
     * @return
     */
    long userEmailRegister(String emailNum, String emailCaptcha);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @param response
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request, HttpServletResponse response);

    /**
     * 使用邮箱登录(后续会改造成使用手机号登录)
     * @param emailNum
     * @param emailCaptcha
     * @param request
     * @param response
     * @return
     */
    LoginUserVO userLoginBySms(String emailNum, String emailCaptcha, HttpServletRequest request, HttpServletResponse response);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 用户注销
     * @param request
     * @param response
     * @return
     */
    boolean userLogout(HttpServletRequest request,HttpServletResponse response);

    /**
     * 刷新秘钥
     * @param request
     * @return
     */
    User refreshKey(User loginUser, HttpServletRequest request);

    /**
     * 发送邮箱/手机验证码
     * @param emailNum
     * @param captchaType
     */
    void sendCode(String emailNum,String captchaType);

    /**
     * 生成图像验证码
     * @param request
     * @param response
     */
    void getCaptcha(HttpServletRequest request, HttpServletResponse response);
}
