package com.liao.ruiji.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.liao.ruiji.dto.DishDto;
import com.liao.ruiji.pojo.Dish;



public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);
}
