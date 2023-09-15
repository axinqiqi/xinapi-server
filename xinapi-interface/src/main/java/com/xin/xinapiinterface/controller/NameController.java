package com.xin.xinapiinterface.controller;

import com.xin.xinapiinterface.common.BaseResponse;
import com.xin.xinapiinterface.common.ResultUtils;
import com.xinapi.xinapiclientsdk.model.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author youshixin
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @PostMapping
    public BaseResponse<List<String>> getNameByPost(@RequestBody User user, HttpServletRequest request) {
        List<String> result = new ArrayList<>();
        String resp = "";
        resp = "你的名字是: " + user.getUsername();
        result.add(resp);
        return ResultUtils.success(result);
    }
}
