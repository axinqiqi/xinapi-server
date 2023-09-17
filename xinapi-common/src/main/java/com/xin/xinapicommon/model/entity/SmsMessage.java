package com.xin.xinapicommon.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 邮箱、手机短信消息对象
 * @author youshixin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsMessage implements Serializable {

//    /**
//     * 手机号码
//     */
//    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 短信
     */
    private String code;

}
