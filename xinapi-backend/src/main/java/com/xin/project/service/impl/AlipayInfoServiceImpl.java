package com.xin.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xin.project.mapper.AlipayInfoMapper;
import com.xin.project.service.AlipayInfoService;
import com.xin.xinapicommon.model.entity.AlipayInfo;
import org.springframework.stereotype.Service;

/**
* @author 86166
* @description 针对表【alipay_info(阿里支付信息表)】的数据库操作Service实现
* @createDate 2023-08-29 22:34:56
*/
@Service
public class AlipayInfoServiceImpl extends ServiceImpl<AlipayInfoMapper, AlipayInfo>
    implements AlipayInfoService {

}




