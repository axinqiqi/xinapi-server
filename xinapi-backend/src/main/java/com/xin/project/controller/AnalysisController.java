package com.xin.project.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.xin.project.annotation.AuthCheck;
import com.xin.project.common.BaseResponse;
import com.xin.project.common.ResultUtils;
import com.xin.project.model.excel.InterfaceInfoInvokeExcel;
import com.xin.project.model.excel.InterfaceInfoOrderExcel;
import com.xin.project.model.vo.InterfaceInfoTotalVO;
import com.xin.project.service.InterfaceInfoService;
import com.xin.project.service.OrderService;
import com.xin.project.service.UserInterfaceInfoService;
import com.xin.xinapicommon.model.entity.InterfaceInfo;
import com.xin.xinapicommon.model.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "7-统计接口")
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private OrderService orderService;

    @ApiOperation(value = "统计接口调用top")
    @GetMapping("top/interface/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceInfoTotalVO>> listTopInterfaceInfo() {
        return ResultUtils.success(userInterfaceInfoService.listTopInterfaceInfo(5));
    }

    @ApiOperation(value = "导出interface_invoke")
    @GetMapping("/top/interface/invoke/excel")
    @AuthCheck(mustRole = "admin")
    public void topInvokeInterfaceInfoExcel(HttpServletResponse response) throws IOException {
        List<InterfaceInfoTotalVO> interfaceInfoVOList = userInterfaceInfoService.listTopInterfaceInfo(5);
        List<InterfaceInfoInvokeExcel> collect = interfaceInfoVOList.stream().map(interfaceInfoVO -> {
            InterfaceInfoInvokeExcel interfaceInfoExcel = new InterfaceInfoInvokeExcel();
            BeanUtils.copyProperties(interfaceInfoVO, interfaceInfoExcel);
            return interfaceInfoExcel;
        }).sorted((a, b) -> b.getTotalNum() - a.getTotalNum()).collect(Collectors.toList());

        String fileName = "interface_invoke.xlsx";
        genExcel(response, fileName, InterfaceInfoInvokeExcel.class, collect);
    }

    @ApiOperation(value = "统计接口购买top")
    @GetMapping("/top/interface/buy")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<OrderVO>> listTopBuyInterfaceInfo() {
        return ResultUtils.success(interfaceBuyTopAnalysis());
    }

    @ApiOperation(value = "导出interface_buy")
    @GetMapping("/top/interface/buy/excel")
    @AuthCheck(mustRole = "admin")
    public void topBuyInterfaceInfoExcel(HttpServletResponse response) throws IOException {
        List<OrderVO> orderVOList = interfaceBuyTopAnalysis();
        List<InterfaceInfoOrderExcel> collect = orderVOList.stream().map(orderVO -> {
            InterfaceInfoOrderExcel interfaceInfoOrderExcel = new InterfaceInfoOrderExcel();
            BeanUtils.copyProperties(orderVO, interfaceInfoOrderExcel);
            return interfaceInfoOrderExcel;
        }).sorted((a, b) -> (int) (b.getTotal() - a.getTotal())).collect(Collectors.toList());
        String fileName = "interface_buy.xlsx";

        genExcel(response, fileName, InterfaceInfoOrderExcel.class, collect);
    }

    private List<OrderVO> interfaceBuyTopAnalysis() {
        return orderService.listTopBuyInterfaceInfo(5).stream().map(order -> {
            Long interfaceId = order.getInterfaceId();
            InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
            OrderVO orderVO = new OrderVO();
            orderVO.setInterfaceId(interfaceId);
            orderVO.setTotal(order.getCount().longValue());
            orderVO.setInterfaceName(interfaceInfo.getName());
            orderVO.setInterfaceDesc(interfaceInfo.getDescription());
            return orderVO;
        }).collect(Collectors.toList());
    }


    private void genExcel(HttpServletResponse response, String fileName, Class entity, List collect) throws IOException {

        String sheetName = "analysis";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        // 创建ExcelWriter对象
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), entity).build();
        // 创建工作表
        WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();

        // 写入数据到工作表
        excelWriter.write(collect, writeSheet);

        // 关闭ExcelWriter对象
        excelWriter.finish();
    }

}
