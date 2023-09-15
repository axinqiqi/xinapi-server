package com.xin.xinapiinterface.controller;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.xin.xinapiinterface.common.BaseResponse;
import com.xin.xinapiinterface.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/day")
public class DayController {

    @GetMapping("/wallpaper")
    public BaseResponse<List<Map<String, String>>> getDayWallpaperUrl() {
        List<Map<String, String>> resultList = new ArrayList<>();
        HashMap<String, Object> paramMap = new HashMap<>(8);
        paramMap.put("type", "json");
        String result = HttpUtil.get("https://api.vvhan.com/api/bing", paramMap);
        Map<String, Object> resultMap = JSONUtil.parseObj(result);
        Map<String, String> data = (Map<String, String>) resultMap.get("data");
        resultList.add(data);
        return ResultUtils.success(resultList);
    }

}
