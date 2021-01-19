package com.lucky.web.result;

import java.util.Date;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/19 0019 11:08
 */
public class Result {

    protected Date time;
    protected int code;
    protected String message;
    protected Object data;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static Result ok(Object data){
        Result result=new Result();
        result.setTime(new Date());
        result.setCode(200);
        result.setMessage("SUCCESS");
        result.setData(data);
        return result;
    }

    public static Result error(Object errorInfo){
        Result result=new Result();
        result.setTime(new Date());
        result.setCode(500);
        result.setMessage("ERROR");
        result.setData(errorInfo);
        return result;
    }
}
