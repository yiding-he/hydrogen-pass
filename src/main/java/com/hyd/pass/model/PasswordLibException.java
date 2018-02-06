package com.hyd.pass.model;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class PasswordLibException extends RuntimeException {

    public PasswordLibException() {
    }

    public PasswordLibException(String message) {
        super(message);
    }

    public PasswordLibException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordLibException(Throwable cause) {
        super(cause);
    }
}
