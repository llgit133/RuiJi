package com.liao.ruiji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liao.ruiji.pojo.Category;

public interface CategoryService extends IService<Category> {
    void removeCategory(Long id);
}
