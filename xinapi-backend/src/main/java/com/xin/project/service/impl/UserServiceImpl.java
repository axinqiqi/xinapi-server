package com.xin.project.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.xin.project.common.ErrorCode;
import com.xin.project.constant.RabbitmqConstant;
import com.xin.project.constant.RedisConstant;
import com.xin.project.exception.BusinessException;
import com.xin.project.mapper.UserMapper;
import com.xin.project.service.UserService;
import com.xin.project.utils.JwtUtils;
import com.xin.project.utils.LeakyBucket;
import com.xin.xinapicommon.model.entity.SmsMessage;
import com.xin.xinapicommon.model.entity.User;
import com.xin.xinapicommon.model.enums.UserRoleEnum;
import com.xin.xinapicommon.model.vo.LoginUserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.xin.project.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "axin";

    @Resource
    private Gson gson;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;

    private static final String REGISTER_SIGN = "register";

    public static final String USER_LOGIN_EMAIL_CODE = "user:login:email:code:";

    public static final String USER_REGISTER_EMAIL_CODE = "user:register:email:code:";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 4 || checkPassword.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 分配 accessKey, secretKey
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
            // 4. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public long userEmailRegister(String emailNum, String emailCaptcha) {
        emailCodeValid(emailNum, emailCaptcha);
        // 校验邮箱是否已经注册过
        synchronized (emailNum.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", emailNum);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱已注册");
            }
            // 给用户分配调用接口的公钥和私钥ak,sk，保证复杂的同时要保证唯一
            String accessKey = DigestUtil.md5Hex(SALT + emailNum + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + emailNum + RandomUtil.randomNumbers(8));
            User user = new User();
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            user.setUserName(emailNum);
            user.setEmail(emailNum);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request, HttpServletResponse response) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3.记录用户的登录态
        return setLoginUser(response, user);
    }

    @Override
    public LoginUserVO userLoginBySms(String emailNum, String emailCaptcha, HttpServletRequest request, HttpServletResponse response) {
        emailCodeValid(emailNum, emailCaptcha);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", emailNum);
        User user = this.getOne(queryWrapper);
        if (ObjUtil.isNull(user)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        // 记录用户的登录态
        return setLoginUser(response, user);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Long userId = JwtUtils.getUserIdByToken(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        String userJson = stringRedisTemplate.opsForValue().get(USER_LOGIN_STATE + userId);
        User user = gson.fromJson(userJson, User.class);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return user;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        User user = getLoginUser(request);
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                Long userId = JwtUtils.getUserIdByToken(request);
                stringRedisTemplate.delete(USER_LOGIN_STATE + userId);
                Cookie timeOutCookie = new Cookie(cookie.getName(), cookie.getValue());
                timeOutCookie.setMaxAge(0);
                response.addCookie(timeOutCookie);
                return true;
            }
        }
        throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
    }

    @Override
    public User refreshKey(User loginUser, HttpServletRequest request) {
        User refresh = refresh(loginUser.getUserAccount());
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userAccount", loginUser.getUserAccount());
        updateWrapper.eq("id", loginUser.getId());
        updateWrapper.set("accessKey", refresh.getAccessKey());
        updateWrapper.set("secretKey", refresh.getSecretKey());
        this.update(updateWrapper);

        loginUser.setAccessKey(refresh.getAccessKey());
        loginUser.setSecretKey(refresh.getSecretKey());

        // 重置登录用户的ak,sk信息
        String userJson = gson.toJson(loginUser);
        stringRedisTemplate.opsForValue().set(USER_LOGIN_STATE + loginUser.getId(), userJson, JwtUtils.EXPIRE, TimeUnit.MILLISECONDS);
        return refresh;
    }

    @Override
    public void sendCode(String emailNum, String captchaType) {
        if (StrUtil.isBlank(captchaType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码为空！");
        }
        // 令牌桶算法实现短信接口的限流，因为手机号码重复发送短信，要进行流量控制
        // 解决同一个手机号的并发问题，锁的粒度非常小，不影响性能。只是为了防止用户第一次发送短信时的恶意调用
        synchronized (emailNum.intern()) {
            Boolean exist = stringRedisTemplate.hasKey(USER_LOGIN_EMAIL_CODE + emailNum);
            if (ObjUtil.isNotNull(exist) && exist) {
                // 1.令牌桶算法对手机短信接口进行限流 具体限流规则为同一个手机号，60s只能发送一次
                long lastTime = 0L;
                LeakyBucket leakyBucket = null;
                if (captchaType.equals(REGISTER_SIGN)) {
                    String strLastTime = stringRedisTemplate.opsForValue().get(USER_REGISTER_EMAIL_CODE + emailNum);
                    if (strLastTime != null) {
                        lastTime = Long.parseLong(strLastTime);
                    }
                    leakyBucket = LeakyBucket.registerLeakyBucket;
                } else {
                    String strLastTime = stringRedisTemplate.opsForValue().get(USER_LOGIN_EMAIL_CODE + emailNum);
                    if (strLastTime != null) {
                        lastTime = Long.parseLong(strLastTime);
                    }
                    leakyBucket = LeakyBucket.loginLeakyBucket;
                }
                if (!leakyBucket.control(lastTime)) {
                    log.info("邮箱发送太频繁了");
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱送太频繁了");
                }
            }
            // 2.符合限流规则，则生成手机短信
            String code = RandomUtil.randomNumbers(4);
            SmsMessage smsMessage = new SmsMessage(emailNum, code);
            // 消息队列异步发送短信，提高短信的吞吐量
            rabbitTemplate.convertAndSend(RabbitmqConstant.EXCHANGE_SMS_INFORM, RabbitmqConstant.ROUTINGKEY_SMS, smsMessage);

            log.info("邮箱对象：" + smsMessage.toString());
            // 更新手机号/邮箱发送短信的时间
            if (captchaType.equals(REGISTER_SIGN)) {
                stringRedisTemplate.opsForValue().set(USER_REGISTER_EMAIL_CODE + emailNum, "" + System.currentTimeMillis() / 1000);
            } else {
                stringRedisTemplate.opsForValue().set(USER_LOGIN_EMAIL_CODE + emailNum, "" + System.currentTimeMillis() / 1000);
            }
        }
    }

    @Override
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        // 前端必须传一个 signature 来作为唯一标识
        String signature = request.getHeader("signature");
        if (StrUtil.isBlank(signature)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        try {
            // 自定义纯数字的验证码（随机4位数字，可重复）
            RandomGenerator randomGenerator = new RandomGenerator("0123456789", 4);
//            LineCaptcha captcha = CaptchaUtil.createLineCaptcha(100, 30);
//            CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(200, 100, 4, 20);
            ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(200, 100, 4, 4);
            captcha.setGenerator(randomGenerator);
            // 设置响应头
            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "No-cache");
            // 输出到页面
            captcha.write(response.getOutputStream());
            // 打印日志
            log.info("captchaId：{} ----生成的验证码:{}", signature, captcha.getCode());
            // 将验证码设置到Redis中,3分钟过期
            stringRedisTemplate.opsForValue().set(RedisConstant.CAPTCHA_PREFIX + signature, captcha.getCode(), 3, TimeUnit.MINUTES);
            // 关闭流
            response.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录用户的登录态，并返回脱敏后的登录用户
     *
     * @param response
     * @param user
     * @return
     */
    private LoginUserVO setLoginUser(HttpServletResponse response, User user) {
        String token = JwtUtils.getJwtToken(user.getId(), user.getUserName());
        log.info("setLoginUser方法获取token,token:{}", token);
        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        response.addCookie(cookie);
        String userJson = gson.toJson(user);
        stringRedisTemplate.opsForValue().set(USER_LOGIN_STATE + user.getId(), userJson, JwtUtils.EXPIRE, TimeUnit.MILLISECONDS);

        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    private User refresh(String userAccount) {
        String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
        String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
        User user = new User();
        user.setAccessKey(accessKey);
        user.setSecretKey(secretKey);
        return user;
    }

    /**
     * 邮箱验证码校验
     *
     * @param emailNum
     * @param emailCode
     * @return
     */
    private void emailCodeValid(String emailNum, String emailCode) {
        String code = stringRedisTemplate.opsForValue().get(RedisConstant.LOGIN_REGISTER_CODE_PRE + emailNum);
        if (StrUtil.isBlank(emailCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱验证码不能为空");
        }

        if (!emailCode.equals(code)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式或邮箱验证码错误");
        }
    }
}

