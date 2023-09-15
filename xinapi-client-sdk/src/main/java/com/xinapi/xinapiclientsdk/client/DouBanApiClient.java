package com.xinapi.xinapiclientsdk.client;

import cn.hutool.http.HttpRequest;

/**
 * DouBanApiClient-DouBanApiClient
 */
public class DouBanApiClient extends CommonApiClient{

    public DouBanApiClient(String accessKey, String secretKey) {
        super(accessKey, secretKey);
    }

    /**
     * 获取豆瓣电影排行
     * @return
     */
    public String getMovieRankUrl(){
        return HttpRequest.get(GATEWAY_HOST+"/api/interface/douban/movieRank")
                .addHeaders(addHeaders("",accessKey,secretKey))
                .execute().body();
    }
}
