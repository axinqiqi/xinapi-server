package com.xin.project.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xin.project.common.ErrorCode;
import com.xin.project.exception.BusinessException;
import com.xin.project.exception.ThrowUtils;
import com.xin.project.mapper.InterfaceInfoMapper;
import com.xin.project.service.InterfaceInfoService;
import com.xin.xinapicommon.model.entity.InterfaceInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2023-06-15 17:15:40
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper,  InterfaceInfo>
    implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String url = interfaceInfo.getUrl();
        String requestParams = interfaceInfo.getRequestParams();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();
        String method = interfaceInfo.getMethod();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name, url, requestParams, requestHeader, responseHeader, method), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(url) && url.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口地址过长");
        }
    }


}




