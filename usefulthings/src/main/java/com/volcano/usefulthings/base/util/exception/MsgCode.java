package com.volcano.usefulthings.base.util.exception;

/**
 * Created by Liuyc on 2017/8/16.
 */
public final class MsgCode {
    /** 正常 */
    public static final int NORMAL = 0;
    /** 未知错误 */
    public static final int UNKNOW = -1;
    /** 校验错误 */
    public static final int VALIDATION = -2;
    /** 数据不存在 */
    public static final int NOT_FOUND = -3;
    /** 数据已存在 */
    public static final int EXISTED = -4;
    /** 用户未登录 */
    public static final int NOT_LOGGED_ON = -5;
}
