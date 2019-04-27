package com.volcano.usefulthings.base.util.exception;

/**
 * Created by Liuyc on 2017/8/16.
 */
public class ServiceException extends RuntimeException {

    /** 错误代码 */
    protected int code = MsgCode.UNKNOW;

    /** 错误消息 */
    protected String msg;

    public ServiceException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public ServiceException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public ServiceException(Exception e) {
        super("未知异常", e);
        this.code = MsgCode.UNKNOW;
        this.msg = e.getMessage();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
