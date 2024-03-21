package com.zhaoyi.yijiaoyou.exception;

import com.zhaoyi.yijiaoyou.common.ErrorCode;

/**
 * 自定义异常类
 *
 * @author zhaoyi
 */
public class BusinessException extends RuntimeException {

    private String description;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.description = errorCode.getDescription();
    }

    /**
     * 带有描述
     *
     * @return
     */
    public String getDescription() {
        return description;
    }
}
