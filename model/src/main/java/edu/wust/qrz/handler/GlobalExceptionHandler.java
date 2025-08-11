package edu.wust.qrz.handler;

import edu.wust.qrz.common.Result;
import edu.wust.qrz.exception.BadRequestException;
import edu.wust.qrz.exception.DatabaseOperateException;
import edu.wust.qrz.exception.MinioOperateException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理请求参数异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Spring参数校验抛出: {}", e.getMessage());
        return Result.fail(400,"参数校验失败: " + e.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     * 处理请求参数缺失异常
     * @param e 请求参数缺失异常
     * @return Result对象，包含错误信息
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestPartException.class)
    public Result handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        log.error("请求参数缺失异常抛出: {}", e.getMessage());
        return Result.fail(400, "请求参数缺失: " + e.getRequestPartName());
    }

    /**
     * 处理Minio操作异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(MinioOperateException.class)
    public Result handleMinioOperateException(MinioOperateException e) {
        log.error("MinIO操作异常抛出: {}", e.getMessage(), e);
        return Result.fail(500, "MinIO操作异常: " + e.getMessage());
    }

    /**
     * 进一步处理请求参数异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public Result handleBadRequestException(BadRequestException e){
        log.error("自定义异常抛出: {}", e.getMessage());
        return Result.fail(400, "参数校验失败：" + e.getMessage());
    }

    /**
     * 处理基本参数类型校验异常
     * @param e
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleConstraintViolationException(ConstraintViolationException e){
        log.error("约束违反异常抛出：{}", e.getMessage());
        return Result.fail(400, "参数校验失败：" + e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleMissingServletRequestParameterException(MissingServletRequestParameterException e){
        log.error("MissingServletRequestParameterException抛出：{}", e.getMessage());
        return Result.fail(400, "参数校验失败：" + e.getMessage());
    }

    /**
     * 处理数据库操作异常
     * @param e
     * @return
     */
    @ExceptionHandler(DatabaseOperateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleDatabaseOperateException(DatabaseOperateException e){
        log.error("数据库操作异常抛出，{}", e.getMessage());
        return Result.fail("数据库操作异常：" + e.getMessage());
    }

    /**
     * 处理其他未知异常
     * @param e 顶层异常
     * @return Result对象，包含错误信息
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("顶层异常抛出: {}", e.getMessage());
        return Result.fail("服务器异常: " + e.getLocalizedMessage());
    }
}
