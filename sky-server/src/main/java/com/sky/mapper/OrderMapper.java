package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import com.sky.vo.TurnoverReportVO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);


    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, List<Long> orders);

    @Select("select id from orders where user_id = #{userId}")
    List<Long> queryById(Long user_id);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id = #{orderId}")
    Orders queryByOrderId(Long orderId);

    void update(Orders orders);

    @Select("select count(*) from orders where status = #{status}")
    Integer queryByStatus(Integer status);

    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrdertimeLT(Integer status, LocalDateTime orderTime);

    @Select("select * from orders where number = #{number}")
    Orders queryByNumber(String number);


    Double getTurnOver(Map map);

    Integer getOrderReport(Map map);

    List<GoodsSalesDTO> getSalesTop10ReportVO(Map map);


}
