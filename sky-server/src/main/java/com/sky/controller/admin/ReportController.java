package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "统计报表相关接口")
@Slf4j
public class ReportController {
    @Autowired
    private ReportService reportService;


    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> getturnOver(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("营业额统计:{},{}",begin,end);
        TurnoverReportVO turnOver = reportService.getTurnOver(begin, end);
        return Result.success(turnOver);

    }

    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> getUserReport(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                              @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("用户统计:{}",begin,end);
        UserReportVO userReport = reportService.getUserReport(begin, end);
        return Result.success(userReport);
    }

    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> getOrderReport(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("订单统计:{}",begin,end);
        OrderReportVO orderReport = reportService.getOrderReport(begin, end);
        return Result.success(orderReport);
    }

    @GetMapping("/top10")
    @ApiOperation("销量tpo10")
    public Result<SalesTop10ReportVO> getSalesTop10ReportVO(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("订单统计:{}",begin,end);
        SalesTop10ReportVO salesTop10ReportVO = reportService.getSalesTop10ReportVO(begin, end);
        return Result.success(salesTop10ReportVO);
    }

}
