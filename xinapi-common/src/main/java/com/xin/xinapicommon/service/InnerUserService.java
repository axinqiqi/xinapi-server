package com.xin.xinapicommon.service;

import com.xin.xinapicommon.model.entity.User;

/**
 * 用户服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public interface InnerUserService {

    /**
     * 数据库中查是否已分配给用户秘钥（根据accessKey拿到用户信息，返回用户信息）
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);

}
