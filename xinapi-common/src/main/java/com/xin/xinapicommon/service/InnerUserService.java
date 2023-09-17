package com.xin.xinapicommon.service;

import com.xin.xinapicommon.model.entity.User;

/**
 * 用户服务
 *
 */
public interface InnerUserService {

    /**
     * 数据库中查是否已分配给用户秘钥（根据accessKey拿到用户信息，返回用户信息）
     * @param accessKey accessKey
     * @return User
     */
    User getInvokeUser(String accessKey);

}
