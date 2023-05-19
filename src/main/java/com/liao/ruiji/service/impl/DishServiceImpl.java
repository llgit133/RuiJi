package com.liao.ruiji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liao.ruiji.dto.DishDto;
import com.liao.ruiji.mapper.DishMapper;
import com.liao.ruiji.pojo.Dish;
import com.liao.ruiji.pojo.DishFlavor;
import com.liao.ruiji.service.DishFlavorService;
import com.liao.ruiji.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //将菜品数据保存到dish表
        this.save(dishDto);

        //获取dishId
        Long dishId = dishDto.getId();

        //将获取到的dishId赋值给dishFlavor的dishId属性
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor dishFlavor : flavors) {
            dishFlavor.setDishId(dishId);
            dishFlavor.setCreateUser(1L);
        }

        //同时将菜品口味数据保存到dish_flavor表
        dishFlavorService.saveBatch(flavors);
    }

//    @Override
//    @Transactional
//    public void saveWithFlavor(DishDto dishDto) {
//        //保存菜品表
//        super.save(dishDto);
//        // 得到disId也就是口味id
//        Long dishId = dishDto.getId();
//
//        // 增加 口味表
//        List<DishFlavor> flavors = dishDto.getFlavors();
//        flavors.stream().map(item -> {
//            item.setDishId(dishId);
//            return item;
//        }).collect(Collectors.toList());
//      // dishFlavorService.saveBatch(dishDto.getFlavors());
//        dishFlavorService.saveBatch(flavors);
//    }

}