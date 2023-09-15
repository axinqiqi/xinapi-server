package com.xin.project.model.dto.user;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户注册请求体
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    @ApiModelProperty(value = "用户账号")
    private String userAccount;

    @ApiModelProperty(value = "用户密码")
    private String userPassword;

    @ApiModelProperty(value = "再次输入密码")
    private String checkPassword;

    @ApiModelProperty(value = "验证码")
    private String captcha;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "qq邮箱")
    private String emailNum;

    @ApiModelProperty(value = "邮箱验证码")
    private String emailCaptcha;
}
