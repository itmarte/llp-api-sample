package com.uxiuer.api.sample.common.exceptions;

/**
 * @Description:
 * @author: imart·deng
 * @date: 2020/6/30 14:36
 */
public class RequestException extends Exception {
    private static final long serialVersionUID = 1L;

    public RequestException() {
        super();
    }

    public RequestException(String message) {
        super(message);
    }

    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestException(Throwable cause) {
        super(cause);
    }
}