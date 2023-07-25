package com.zhl.zconsistency.exceptions;

/**
 * @author hailang.zhang
 * @since 2023-07-25
 */
public class ConsistencyException extends RuntimeException{

    public ConsistencyException() {
    }

    public ConsistencyException(Exception e) {
        super(e);
    }

    public ConsistencyException(String message) {
        super(message);
    }
}