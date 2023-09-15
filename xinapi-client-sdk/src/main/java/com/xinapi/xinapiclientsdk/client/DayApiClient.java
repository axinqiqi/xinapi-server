package com.xinapi.xinapiclientsdk.client;

import cn.hutool.http.HttpRequest;

/**
 * DayController-DayApiClient
 */
public class DayApiClient extends CommonApiClient{

    public DayApiClient(String accessKey, String secretKey) {
        super(accessKey, secretKey);
    }

    /**
     * 获取每日壁纸
     * @return
     */
    public String getDayWallpaperUrl(){
        return HttpRequest.get(GATEWAY_HOST+"/api/interface/day/wallpaper")
                .addHeaders(addHeaders("",accessKey,secretKey))
                .execute().body();
    }
}
