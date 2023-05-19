package com.liao.ruiji.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

 // 全局的SQL异常处理
@Slf4j
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GlobalExceptionHandler {
    /**
     *
     *
     *
     *
     * */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException exception) {

        log.error(exception.getMessage());
        //如果包含Duplicate entry，则说明有条目重复
        if (exception.getMessage().contains("Duplicate entry")) {
            //对字符串切片
            String[] split = exception.getMessage().split(" ");
            //字符串格式是固定的，所以这个位置必然是username
            String username = split[2];
            //拼串作为错误信息返回
            return Result.error("用户名" + username + "已存在");
        }
        //如果是别的错误那我也没招儿了
        return Result.error(exception.getMessage());
    }

     @ExceptionHandler(CustomException.class)
     public Result<String> exceptionHandler(CustomException ex) {
         log.error("111111111111111111111"+ex.getMessage());
         //如果是别的错误那我也没招儿了
         return Result.error(ex.getMessage());
     }
}
