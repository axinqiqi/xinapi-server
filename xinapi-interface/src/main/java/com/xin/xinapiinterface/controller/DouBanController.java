package com.xin.xinapiinterface.controller;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.xin.xinapiinterface.common.BaseResponse;
import com.xin.xinapiinterface.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/douban")
public class DouBanController {

    @GetMapping("/movieRank")
    public BaseResponse<List<Map<String, String>>> getMovieRankUrl() {
        String result = HttpUtil.get("https://api.vvhan.com/api/douban");
        Map<String, Object> resultMap = JSONUtil.parseObj(result);
        List<Map<String, String>> data = (List<Map<String, String>>) resultMap.get("data");
        return ResultUtils.success(data);
    }

}
