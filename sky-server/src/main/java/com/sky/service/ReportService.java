package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalTime;

public interface ReportService {
    TurnoverReportVO getTurnOver(LocalDate begin,LocalDate end);

    UserReportVO getUserReport(LocalDate begin,LocalDate end);

    OrderReportVO getOrderReport(LocalDate begin, LocalDate end);

    SalesTop10ReportVO getSalesTop10ReportVO(LocalDate begin, LocalDate end);

    void exportBusinessData(HttpServletResponse response);
}
