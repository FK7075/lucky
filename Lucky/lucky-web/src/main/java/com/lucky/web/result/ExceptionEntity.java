package com.lucky.web.result;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/18 0018 11:12
 */
public class ExceptionEntity {


    /** 异常*/
    private String ex;
    /** 异常说明*/
    private String message;
    /** 堆栈信息*/
    private String stackMassage;

    public String getEx() {
        return ex;
    }

    public void setEx(String ex) {
        this.ex = ex;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackMassage() {
        return stackMassage;
    }

    public void setStackMassage(String stackMassage) {
        this.stackMassage = stackMassage;
    }

    public ExceptionEntity(Exception e){
        ex=e.toString();
        message=e.getMessage();
        StringWriter sw=new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        stackMassage=sw.toString();
    }

}
