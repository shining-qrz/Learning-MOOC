package edu.wust.qrz.exception;

public class DatabaseOperateException extends RuntimeException{
    public DatabaseOperateException(String message) {
        super(message);
    }

    public DatabaseOperateException(String message, Throwable cause) {
        super(message, cause);
    }
}
