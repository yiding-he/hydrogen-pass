package com.hyd.pass.conf;

/**
 * (description)
 * created at 2017/7/6
 *
 * @author yidin
 */
public class ConfigException extends RuntimeException {

    public ConfigException() {
    }

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigException(Throwable cause) {
        super(cause);
    }
}
