package com.xin.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xin.xinapicommon.model.entity.Order;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_order(订单表)】的数据库操作Mapper
* @createDate 2023-08-25 11:10:02
* @Entity generator.domain.Order
*/
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 获取前 sum 购买数量的接口
     * @param sum
     * @return
     */
    List<Order> listTopBuyInterfaceInfo(int sum);
}




