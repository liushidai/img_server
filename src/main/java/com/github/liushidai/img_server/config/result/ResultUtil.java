package com.github.liushidai.img_server.config.result;

public class ResultUtil<T> {
    private final Result<T> result;

    public ResultUtil() {
        result = new Result<>();
        result.success = true;
    }

    /**
     * 成功返回结果
     */
    public static <T> Result<T> success() {
        return new ResultUtil<T>().setSuccessMsg();
    }

    /**
     * 成功返回结果
     *
     * @param msg 提示信息
     */
    public static <T> Result<T> success(String msg) {
        return new ResultUtil<T>().setSuccessMsg(msg);
    }

    /**
     * 成功返回结果
     *
     * @param t 返回的数据
     */
    public static <T> Result<T> data(T t) {
        return new ResultUtil<T>().setData(t);
    }

    /**
     * 失败返回结果
     */
    public static <T> Result<T> failed() {
        return new ResultUtil<T>().setFailedMsg();
    }

    /**
     * 失败返回结果
     *
     * @param message 错误信息
     */
    public static <T> Result<T> failed(String message) {
        return new ResultUtil<T>().setFailedMsg(message);
    }

    /**
     * 成功返回结果
     *
     * @param t   返回的数据
     * @param msg 提示信息
     */
    public static <T> Result<T> data(T t, String msg) {
        return new ResultUtil<T>().setData(t, msg);
    }

    public Result<T> setSuccessMsg() {
        result.success = true;
        return this.result;
    }

    public Result<T> setSuccessMsg(String msg) {
        result.success = true;
        this.result.message = msg;
        return this.result;
    }

    public Result<T> setData(T t) {
        result.success = true;
        this.result.result = t;
        return this.result;
    }

    public Result<T> setData(T t, String msg) {
        result.success = true;
        this.result.result = t;
        this.result.message = msg;
        return this.result;
    }

    public Result<T> setFailedMsg() {
        result.success = false;
        return this.result;
    }

    public Result<T> setFailedMsg(String msg) {
        result.success = false;
        this.result.message = msg;
        return this.result;
    }
}
