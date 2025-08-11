package edu.wust.qrz.exception;

import java.io.Serial;

public class MinioOperateException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public MinioOperateException(String message) {
        super(message);
    }

    public MinioOperateException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinioOperateException(Throwable cause) {
        super(cause);
    }
}
