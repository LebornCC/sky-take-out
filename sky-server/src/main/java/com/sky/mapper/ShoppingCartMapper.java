package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
public interface ShoppingCartMapper {
    @Update("update shopping_cart set  number = #{number}")
    void update(ShoppingCart shoppingCart);

    @Insert("insert into shopping_cart (name, user_id, dish_id, setmeal_id, dish_flavor, number, amount, image, create_time) " +
            " values (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{image},#{createTime})")
    void insert(ShoppingCart shoppingCart);


    List<ShoppingCart> list(ShoppingCart shoppingCart);

    @Delete("delete from shopping_cart where user_id = #{userId}")
    void delete(ShoppingCart shoppingCart);

    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(ShoppingCart shoppingCart);

    void insertBatch(List<ShoppingCart> shoppingCarts);

}
