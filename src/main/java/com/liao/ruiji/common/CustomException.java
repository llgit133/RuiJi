package com.liao.ruiji.common;

public class CustomException extends RuntimeException{

    // 把提示信息传进来
    public CustomException(String msg){
        super(msg);
    }
}