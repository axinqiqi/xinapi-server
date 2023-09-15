package com.xin.project.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xin.project.common.BaseResponse;
import com.xin.project.common.ErrorCode;
import com.xin.project.common.ResultUtils;
import com.xin.project.constant.CommonConstant;
import com.xin.project.exception.BusinessException;
import com.xin.project.model.dto.order.OrderAddRequest;
import com.xin.project.model.dto.order.OrderQueryRequest;
import com.xin.project.model.enums.OrderStatusEnum;
import com.xin.project.service.InterfaceInfoService;
import com.xin.project.service.OrderService;
import com.xin.project.service.UserService;
import com.xin.xinapicommon.model.entity.InterfaceInfo;
import com.xin.xinapicommon.model.entity.Order;
import com.xin.xinapicommon.model.entity.User;
import com.xin.xinapicommon.model.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "4-订单接口")
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private UserService userService;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @ApiOperation(value = "添加")
    @PostMapping("/addOrder")
    public BaseResponse<Order> interfaceTOrder(@RequestBody OrderAddRequest orderAddRequest, HttpServletRequest request) {
        if (orderAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Order order = new Order();
        BeanUtils.copyProperties(orderAddRequest, order);
        User loginUser = userService.getLoginUser(request);
        if (ObjectUtil.isNull(loginUser)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        orderService.validOrder(order, true);

        Order newOrder = orderService.addOrder(order, request);
        return ResultUtils.success(newOrder);
    }

    @ApiOperation(value = "分页获取列表")
    @GetMapping("/list/page")
    public BaseResponse<Page<OrderVO>> listPageOrder(OrderQueryRequest orderQueryRequest, HttpServletRequest request) {
        if (orderQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer type = Integer.parseInt(orderQueryRequest.getType());
        if (!OrderStatusEnum.getValues().contains(type)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = orderQueryRequest.getCurrent();
        long size = orderQueryRequest.getPageSize();
        String sortField = orderQueryRequest.getSortField();
        String sortOrder = orderQueryRequest.getSortOrder();
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId()).eq("status", type);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);

        Page<Order> orderPage = orderService.page(new Page<>(current, size), queryWrapper);

        Page<OrderVO> orderVOPage = new Page<>(orderPage.getCurrent(),orderPage.getSize(),orderPage.getTotal());
        List<OrderVO> orderVOList = orderPage.getRecords().stream().map(order -> {
            Long interfaceId = order.getInterfaceId();
            InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            orderVO.setTotal(Long.valueOf(order.getCount()));
            orderVO.setTotalAmount(order.getTotalAmount());
            orderVO.setOrderNumber(order.getOrderSn());

            orderVO.setInterfaceName(interfaceInfo.getName());
            orderVO.setInterfaceDesc(interfaceInfo.getDescription());
            orderVO.setExpirationTime(DateUtil.offset(order.getCreateTime(), DateField.MINUTE, 30));
            return orderVO;
        }).collect(Collectors.toList());
        orderVOPage.setRecords(orderVOList);
        return ResultUtils.success(orderVOPage);
    }


}
