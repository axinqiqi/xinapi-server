package com.xin.project.utils;

import com.xin.project.config.QQEmailConfig;
import com.xin.project.constant.RedisConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.concurrent.TimeUnit;


@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageOperation {

    private SimpleEmail mail = new SimpleEmail();

    public void sendMessage(String targetEmail, String code, RedisTemplate<String, String> redisTemplate) {
        try {
            // 设置邮箱服务器信息
            mail.setSslSmtpPort(QQEmailConfig.PORT);
            mail.setHostName(QQEmailConfig.HOST);
            // 设置密码验证器
            mail.setAuthentication(QQEmailConfig.EMAIL, QQEmailConfig.PASSWORD);
            // 设置邮件发送者
            mail.setFrom(QQEmailConfig.EMAIL);
            // 设置邮件接收者
            mail.addTo(targetEmail);
            // 设置邮件编码
            mail.setCharset("UTF-8");
            // 设置邮件主题
            mail.setSubject("Xin API");

            //设置数据的5分钟有效期限
            redisTemplate.opsForValue().set(RedisConstant.LOGIN_REGISTER_CODE_PRE + targetEmail, code, 5, TimeUnit.MINUTES);
            // 设置邮件内容
            mail.setMsg("Xin API： 您的注册 or 登录 验证码为：" + code + ",验证码5分钟内有效。");
            // 设置邮件发送时间
            mail.setSentDate(new Date());
            // 发送邮件
            mail.send();
        } catch (EmailException e) {
            e.printStackTrace();
            log.error("Error sending email: {}", e.getMessage());
        }
    }
}
