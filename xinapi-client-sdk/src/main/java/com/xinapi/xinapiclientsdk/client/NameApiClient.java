package com.xinapi.xinapiclientsdk.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.xinapi.xinapiclientsdk.model.User;

import java.util.HashMap;

/**
 * @author youshixin
 */
public class NameApiClient extends CommonApiClient {
    public NameApiClient(String accessKey, String secretKey) {
        super(accessKey, secretKey);
    }

    public String getNameByGet(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>(8);
        paramMap.put("name", name);

        return HttpUtil.get(GATEWAY_HOST + "/api/name", paramMap);
    }


    public String getNameByPost(User user) {
        String jsonStr = JSONUtil.toJsonStr(user);

        HttpResponse execute = HttpRequest.post(GATEWAY_HOST + "/api/interface/name")
                .addHeaders(addHeaders(jsonStr, accessKey, secretKey))
                .body(jsonStr)
                .execute();

        return execute.body();
    }

}
