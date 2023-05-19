package com.liao.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liao.ruiji.common.Result;
import com.liao.ruiji.pojo.Category;
import com.liao.ruiji.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜品及套餐分类 前端控制器
 * </p>
 *
 * @author cc
 * @since 2022-05-30
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 1.新增菜品分类
     * @param category 接收菜品分类对象
     * @return
     */
    @PostMapping("")
    public Result<Category> save(@RequestBody Category category){
        log.info("新增菜品分类");
        categoryService.save(category);
        return Result.success(category);
    }

    /**
     * 2.菜品列表功能
     * @param page 第几页
     * @param pageSize 每页查几条数据
     * @return
     */
    @GetMapping("page")
    public Result<Page> listCategory(int page,int pageSize){
        //分页构造器
        Page pageInfo = new Page(page, pageSize);
        //过滤条件
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.orderByDesc(Category::getSort);
        categoryService.page(pageInfo,lambdaQueryWrapper);
        return Result.success(pageInfo);
    }


    /**
     * 3.删除菜品种类
     * @param ids 分类ID
     * @return
     */

    // 注意，前端的即为ids因此要对应 不然后端无法接收
    @DeleteMapping()
    public Result<String> delCategory(Long ids){
//        categoryService.removeById(id);  这是mp给的方法

        log.info("将要删除的分类id:{}",ids);
        // 自己实现的remove方法
        categoryService.removeCategory(ids);
        return Result.success("删除成功");
    }


    /**
     * 4.更新菜品分类
     *
     * @param category 传回来的菜品分类对象
     */
    @PutMapping()
    public Result<String> updateCategory(@RequestBody Category category) {
        log.info("更新种类{}", category);
        // 自动传入更新时间
        categoryService.updateById(category);
        return Result.success("菜品种类更新完成");
    }


    /**
     * 5.菜品新增页面菜品下拉列表
     * @param category 从前端接收一个type=1的标注，目的是在分类表中，菜品分类是1，套餐分类是2，把二者区分开
     * @return
     */
    @GetMapping("/list")
    // @RequestBody 处理的是前端的json格式的数据，
    public Result<List> listShowCategory(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper();

        //MP也支持把对象先判断一下，非空才能进行查询
        lambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());

        //lambdaQueryWrapper.eq(Category::getType, category.getType());
        //按时间倒叙排序
        lambdaQueryWrapper.orderByDesc(Category::getUpdateTime);

        //查询
        List<Category> categoryList = categoryService.list(lambdaQueryWrapper);
        return Result.success(categoryList);
    }
}
