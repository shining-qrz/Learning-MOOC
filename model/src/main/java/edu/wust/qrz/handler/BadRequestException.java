package edu.wust.qrz.handler;

/**
 * 自定义异常类，用于处理请求参数错误
 */
public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
