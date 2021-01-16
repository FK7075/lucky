package com.lucky.utils.exception;

import com.lucky.utils.annotation.Nullable;
import com.lucky.utils.io.utils.NestedExceptionUtils;

import java.io.IOException;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/15 0015 10:30
 */
@SuppressWarnings("serial")
public class NestedIOException extends IOException {

    static {
        // Eagerly load the NestedExceptionUtils class to avoid classloader deadlock
        // issues on OSGi when calling getMessage(). Reported by Don Brown; SPR-5607.
        NestedExceptionUtils.class.getName();
    }


    /**
     * Construct a {@code NestedIOException} with the specified detail message.
     * @param msg the detail message
     */
    public NestedIOException(String msg) {
        super(msg);
    }

    /**
     * Construct a {@code NestedIOException} with the specified detail message
     * and nested exception.
     * @param msg the detail message
     * @param cause the nested exception
     */
    public NestedIOException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }


    /**
     * Return the detail message, including the message from the nested exception
     * if there is one.
     */
    @Override
    @Nullable
    public String getMessage() {
        return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
    }

}