package com.xin.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xin.xinapicommon.model.entity.Order;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Administrator
* @description 针对表【t_order(订单表)】的数据库操作Service
* @createDate 2023-08-25 11:10:02
*/
public interface OrderService extends IService<Order> {

    void validOrder(Order order, boolean add);

    /**
     * 创建订单
     * @param order
     * @param request
     * @return
     */
    Order addOrder(Order order, HttpServletRequest request);

    /**
     * 获取前 limit 购买数量的接口
     * @param num
     * @return
     */
    List<Order> listTopBuyInterfaceInfo(int num);
}
