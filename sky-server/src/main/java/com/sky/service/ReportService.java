package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ReportService {
    TurnoverReportVO getTurnOver(LocalDate begin,LocalDate end);
}
