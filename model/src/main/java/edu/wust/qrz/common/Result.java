package edu.wust.qrz.common;

import lombok.Data;

@Data
public class Result {
    private Integer code;
    private String msg;
    private Object data;

    public static Result ok(){
        Result result = new Result();
        result.setCode(200);
        result.setMsg("响应成功");
        return result;
    }

    public static Result ok(String msg, Object data){
        Result result = new Result();
        result.setCode(200);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static Result fail(){
        Result result = new Result();
        result.setCode(500);
        result.setMsg("服务器异常");
        return result;
    }

    public static Result fail(String msg){
        Result result = new Result();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
}
