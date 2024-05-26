package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //判断是否存在购物车中
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list != null && list.size() >0){
            //存在 更新
            shoppingCart = list.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber()+1);
            shoppingCartMapper.update(shoppingCart);
        }else {
            //不存在 插入
            Long dishId = shoppingCartDTO.getDishId();


            if (dishId != null){
                List<Long> dishids = new ArrayList<>();
                dishids.add(shoppingCartDTO.getDishId());
                List<Dish> dishes = dishMapper.getByIds(dishids);
                shoppingCart.setName(dishes.get(0).getName());
                shoppingCart.setImage(dishes.get(0).getImage());
                shoppingCart.setAmount(dishes.get(0).getPrice());

            }else {
                List<Long> setmealids = new ArrayList<>();
                setmealids.add(shoppingCartDTO.getSetmealId());
                List<Setmeal> setmeals = setmealMapper.geyById(setmealids);
                shoppingCart.setName(setmeals.get(0).getName());
                shoppingCart.setImage(setmeals.get(0).getImage());
                shoppingCart.setAmount(setmeals.get(0).getPrice());

            }

            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }

    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        return shoppingCartMapper.list(ShoppingCart.builder().userId(BaseContext.getCurrentId()).build());
    }

    @Override
    public void cleanShopppingCart() {
        shoppingCartMapper.delete(ShoppingCart.builder().userId(BaseContext.getCurrentId()).build());
    }

    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {


        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list != null && list.size() >0){
            shoppingCart = list.get(0);
            if (shoppingCart.getNumber() > 1){
                shoppingCart.setNumber(shoppingCart.getNumber() -1);
                shoppingCartMapper.update(shoppingCart);
            }else {
                shoppingCartMapper.deleteById(shoppingCart);
            }
        }

    }
}
