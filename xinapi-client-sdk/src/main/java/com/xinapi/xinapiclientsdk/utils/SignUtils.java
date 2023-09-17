package com.xinapi.xinapiclientsdk.utils;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * 生成签名utils
 *
 * @author youshixin
 * 2023-7-23 16:12
 */
public class SignUtils {
    public static String getSign(String body, String secretKey){
        return DigestUtil.sha256Hex(body+ "." + secretKey);
    }
}
