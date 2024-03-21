package com.zhaoyi.yijiaoyou.exception;
import com.zhaoyi.yijiaoyou.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @author zhaoyi
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 捕获制定异常 并做处理
     * @param businessException
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessException(BusinessException businessException){
        //要把错误信息详细传递给前端  同时要给予后端发生的错误是什么
        log.error("BusinessException:"+businessException.getDescription());
        return BaseResponse.error(businessException.getDescription());
    }

}
