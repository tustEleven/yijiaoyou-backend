package com.zhaoyi.yijiaoyou.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 响应格式
 *
 * @param <T>
 * @author zhaoyi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> implements Serializable {

    /**
     * 相应是否成功 0 失败  1成功
     */
    private int code;

    /**
     * 相应的数据
     */
    private T data;

    /**
     * 额外的信息
     */
    private String msg;

    /**
     * 带有数据的相应
     *
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setData(data);
        baseResponse.setCode(1);
        return baseResponse;
    }

    /**
     * 没有数据的相应
     *
     * @return
     */
    public static BaseResponse success() {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(1);
        return baseResponse;
    }

    /**
     * 提示错误的相应
     *
     * @param msg
     * @return
     */
    public static BaseResponse error(String msg) {
        BaseResponse<Object> baseResponse = new BaseResponse<>();
        baseResponse.setMsg(msg);
        return baseResponse;
    }

}
