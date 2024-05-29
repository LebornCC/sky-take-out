package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private UserMapper userMapper;
    @Override
    public TurnoverReportVO getTurnOver(LocalDate begin, LocalDate end) {
        List<LocalDate> localDateList = new ArrayList<>();
        localDateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }

        List<Double> turnoverReportVOList = new ArrayList<>();
        for (LocalDate localDate : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();
            map.put("beginTime",beginTime);
            map.put("endTime",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnOver = orderMapper.getTurnOver(map);
            if (turnOver == null){
                turnOver = 0.0;
            }
            turnoverReportVOList.add(turnOver);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(localDateList,","))
                .turnoverList(StringUtils.join(turnoverReportVOList,","))
                .build();




    }

    @Override
    public UserReportVO getUserReport(LocalDate begin, LocalDate end) {
        List<LocalDate> localDateList = new ArrayList<>();
        localDateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }
        List<Integer> Alluser = new ArrayList<>();
        List<Integer> NewUser = new ArrayList<>();
        for (LocalDate localDate : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();
            map.put("beginTime",beginTime);
            Integer All = userMapper.getUserReport(map);
            map.put("endTime",endTime);
            Integer New = userMapper.getUserReport(map);
            if (All == null){
                All = 0;
            }
            if (New == null){
                New = 0;
            }
            Alluser.add(All);
            NewUser.add(New);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(localDateList,","))
                .newUserList(StringUtils.join(NewUser,","))
                .totalUserList(StringUtils.join(Alluser,","))
                .build();

    }

    @Override
    public OrderReportVO getOrderReport(LocalDate begin, LocalDate end) {
        List<LocalDate> localDateList = new ArrayList<>();
        localDateList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            localDateList.add(begin);
        }

        List<Integer> orderAll = new ArrayList<>();
        List<Integer> orderSuccess = new ArrayList<>();
        for (LocalDate localDate : localDateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();
            map.put("beginTime",beginTime);
            map.put("endTime",endTime);
            Integer all = orderMapper.getOrderReport(map);
            map.put("status",Orders.COMPLETED);
            Integer success = orderMapper.getOrderReport(map);
            orderAll.add(all);
            orderSuccess.add(success);
        }
        Integer total = orderAll.stream().reduce(Integer::sum).get();
        Integer valid = orderSuccess.stream().reduce(Integer::sum).get();
        Double rate = (double) 0;
        if (total != 0){
            rate = valid.doubleValue()/total;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(localDateList,","))
                .orderCompletionRate(rate)
                .orderCountList(StringUtils.join(orderAll,","))
                .totalOrderCount(total)
                .validOrderCount(valid)
                .validOrderCountList(StringUtils.join(orderSuccess,","))
                .build();


    }

    @Override
    public SalesTop10ReportVO getSalesTop10ReportVO(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        Map map = new HashMap();
        map.put("beginTime",beginTime);
        map.put("endTime",endTime);
        List<GoodsSalesDTO> salesTop10ReportVO = orderMapper.getSalesTop10ReportVO(map);
        String nameList = StringUtils.join(salesTop10ReportVO.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()), ",");
        String numberList = StringUtils.join(salesTop10ReportVO.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList()), ",");
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    @Override
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate beginTime = LocalDate.now().minusDays(30);
        LocalDate endTime = LocalDate.now().minusDays(1);
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(beginTime,LocalTime.MIN), LocalDateTime.of(endTime,LocalTime.MAX));

        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/template.xlsx");
            XSSFWorkbook excel = new XSSFWorkbook(in);
            XSSFSheet sheet1 = excel.getSheet("Sheet1");
            XSSFRow row = sheet1.getRow(1);
            row.getCell(1).setCellValue("时间"+beginTime+"至"+endTime);

            row = sheet1.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());


            row = sheet1.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());


            for (int i = 0; i < 30; i++) {
                LocalDate now = beginTime.plusDays(i);
                businessData = workspaceService.getBusinessData(LocalDateTime.of(now, LocalTime.MIN), LocalDateTime.of(now, LocalTime.MAX));
                row = sheet1.getRow(7+i);
                row.getCell(1).setCellValue(now.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            outputStream.flush();
            outputStream.close();
            excel.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
        }
    }
}
