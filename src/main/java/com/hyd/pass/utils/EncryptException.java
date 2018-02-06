package com.hyd.pass.utils;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class EncryptException extends RuntimeException {

    public EncryptException() {
    }

    public EncryptException(String message) {
        super(message);
    }

    public EncryptException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncryptException(Throwable cause) {
        super(cause);
    }
}
