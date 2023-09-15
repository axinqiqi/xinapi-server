package com.xin.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xin.project.common.ErrorCode;
import com.xin.project.exception.BusinessException;
import com.xin.project.mapper.InterfaceChargingMapper;
import com.xin.project.mapper.InterfaceInfoMapper;
import com.xin.project.mapper.OrderMapper;
import com.xin.project.service.InterfaceChargingService;
import com.xin.project.service.OrderService;
import com.xin.project.utils.OrderMqUtils;
import com.xin.xinapicommon.model.entity.InterfaceCharging;
import com.xin.xinapicommon.model.entity.InterfaceInfo;
import com.xin.xinapicommon.model.entity.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Administrator
 * @description 针对表【t_order(订单表)】的数据库操作Service实现
 * @createDate 2023-08-25 11:10:02
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
        implements OrderService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Resource
    private InterfaceChargingMapper interfaceChargingMapper;

    @Resource
    private InterfaceChargingService interfaceChargingService;

    @Resource
    private OrderMqUtils orderMqUtils;

    @Override
    public void validOrder(Order order, boolean add) {
        if (order == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = order.getUserId();
        Long interfaceId = order.getInterfaceId();
        Double charging = order.getCharging();
        Integer count = order.getCount();
        BigDecimal totalAmount = order.getTotalAmount();
        // 创建时，参数不能为空
        if (add) {

        }
        // 有参数则校验
        if (ObjectUtil.isNull(userId) || ObjectUtil.isNull(interfaceId) || ObjectUtil.isNull(count) || ObjectUtil.isNull(totalAmount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (count <= 0 || totalAmount.compareTo(new BigDecimal(0)) < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectById(interfaceId);
        if (ObjectUtil.isNull(interfaceInfo)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口不存在");
        }
        // 后端校验订单总价格
        double temp = charging * count;
        BigDecimal bd = new BigDecimal(temp);
        double finalPrice = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (finalPrice != totalAmount.doubleValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "价格错误");
        }
        // 判断接口调用库存是否足够
        QueryWrapper<InterfaceCharging> interfaceChargingQueryWrapper = new QueryWrapper<>();
        interfaceChargingQueryWrapper.eq("interfaceId", interfaceId);
        InterfaceCharging interfaceCharging = interfaceChargingMapper.selectOne(interfaceChargingQueryWrapper);
        Integer availablePieces = interfaceCharging.getAvailablePieces();
        if (availablePieces <= 0 || availablePieces - count <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口库存不足");
        }
    }

    @Transactional
    @Override
    public Order addOrder(Order order, HttpServletRequest request) {
        Long userId = order.getUserId();
        Long interfaceId = order.getInterfaceId();
        Integer count = order.getCount();
        // 扣减接口库存 远程调用实现
        UpdateWrapper<InterfaceCharging> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("availablePieces = availablePieces - " + count)
                .eq("interfaceId", interfaceId).gt("availablePieces", count);
        boolean updateStockResult = interfaceChargingService.update(updateWrapper);
        if (!updateStockResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "扣减库存失败");
        }
        // 数据库保存订单数据
        String orderNum = generateOrderNum(userId);
        order.setOrderSn(orderNum);
        boolean save = this.save(order);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存订单失败");
        }
        // 消息队列发送延时消息
        orderMqUtils.sendOrderSnInfo(order);
        return this.getById(order.getId());
    }

    @Override
    public List<Order> listTopBuyInterfaceInfo(int num) {
        return getBaseMapper().listTopBuyInterfaceInfo(num);
    }

    /**
     * 生成订单号
     *
     * @return
     */
    private String generateOrderNum(Long userId) {
        String timeId = IdWorker.getTimeId();
        String substring = timeId.substring(0, timeId.length() - 15);
        return substring + RandomUtil.randomNumbers(5) + userId;
    }
}




