package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
        Long id = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() >0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            dishFlavorMapper.insert(flavors);
        }




    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        List<Dish> dishes = dishMapper.getByIds(ids);
        dishes.forEach(dish -> {
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });

        List<Long> setmealIdsByDishIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIdsByDishIds !=null && setmealIdsByDishIds.size() > 0 ){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        dishMapper.deleteById(ids);
        dishFlavorMapper.deleteByDishId(ids);


    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        List<Dish> dishes = dishMapper.getByIds(ids);
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dishes.get(0),dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        List<Long> ids = new ArrayList<>();
        ids.add(dish.getId());
        dishFlavorMapper.deleteByDishId(ids);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() >0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
            });

            dishFlavorMapper.insert(flavors);
        }

    }

    @Override
    public void starOrStop(Integer status, Long id) {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        dishMapper.deleteById(ids);
        List<Long> setmealIdsByDishIds = setmealDishMapper.getSetmealIdsByDishIds(ids);

        if (setmealIdsByDishIds != null && setmealIdsByDishIds.size() >0){
            for (Long setmealIdsByDishId : setmealIdsByDishIds) {
                Setmeal setmeal = new Setmeal();
                setmeal.setId(setmealIdsByDishId);
                setmeal.setStatus(status);

                setmealMapper.update(setmeal);

            }
        }
    }
}
