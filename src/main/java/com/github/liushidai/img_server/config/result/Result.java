package com.github.liushidai.img_server.config.result;


import java.io.Serializable;

/**
 * @author 前后端交互数据标准
 */
public class Result<T> implements Serializable {

    /**
     * 成功标志
     */
    boolean success;
    /**
     * 消息
     */
    String message;
    /**
     * 时间戳
     */
    long timestamp = System.currentTimeMillis();
    /**
     * 结果对象
     */
    T result;

    public Result() {
    }

    public Result(boolean success, String message, long timestamp, T result) {
        this.success = success;
        this.message = message;
        this.timestamp = timestamp;
        this.result = result;
    }
}
