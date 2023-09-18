package com.xin.project.utils;

import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;

/**
 * @author helen
 * @since 2019/10/16
 */
@Slf4j
public class JwtUtils {

    //常量
    public static final int EXPIRE = 1000 * 60 * 60 * 24; //token过期时间，一天
//    public static final int EXPIRE = 1000 * 60; //token过期时间，一分钟

    public static final String SECRET = "GESGFGASDFAsgddsfagearsafsgaaSGHJSE"; //秘钥

    //生成token字符串的方法
    public static String getJwtToken(Long id, String userName) {
        return Jwts.builder()
                //1.JWT头部分
                .setHeaderParam("typ", "JWT")           //typ属性表示令牌的类型，JWT令牌统一写为JWT
                .setHeaderParam("alg", "HS256")         //alg属性表示签名使用的算法，默认为HMAC SHA256（写为HS256）

                //2.JWT有效载荷部分
//               iss：发行人
//               exp：到期时间
//               sub：主题
//               aud：用户
//               nbf：在此之前不可用
//               iat：发布时间
//               jti：JWT ID用于标识该JWT

                .setSubject("api-user")                    //用于对数据进行分类
                .setIssuedAt(new Date())                    //设置数据发布(创建)时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))   //设置数据的过期时间

                //设置token主体部分(也属于有效载荷部分) ，存储用户信息
                .claim("id", id)
                .claim("userName", userName)

                //3.签名哈希部分
                //签名哈希部分是对上面两部分数据签名，通过指定的算法生成哈希，以确保数据不会被篡改。
                //首先，需要指定一个密码（secret）。该密码仅仅为保存在服务器中，并且不能向用户公开。然后，使用
                //标头中指定的签名算法（默认情况下为HMAC SHA256）根据以下公式生成签名。
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    /**
     * 判断token是否存在与有效
     *
     * @param jwtToken token
     * @return boolean
     */
    public static boolean checkToken(String jwtToken) {
        if (StrUtil.isEmpty(jwtToken)) {
            return false;
        }
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据token字符串获取token字符串中有效载荷部分的数据(有效载荷部分包含用户的数据)
     *
     * @param request http请求
     * @return long
     */
    public static Long getUserIdByToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        log.info("getUserIdByToken方法获取cookies,cookies是:{}", Arrays.toString(cookies));
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                String token = cookie.getValue();
                log.info("getUserIdByToken方法获取token,token:{}", token);
                if (!StrUtil.isEmpty(token)) {
                    try {
                        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
                        Claims claims = claimsJws.getBody();
                        return Long.parseLong(claims.get("id").toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}