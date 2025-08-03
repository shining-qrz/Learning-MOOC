package edu.wust.qrz.exception;

import edu.wust.qrz.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理其他未知异常
     * @param e 顶层异常
     * @return Result对象，包含错误信息
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("顶层异常抛出: {}", e.getMessage());
        return Result.fail("服务器异常: " + e.getMessage());
    }
}
