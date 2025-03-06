package com.sharedule.app.exception;

public class BackendErrorException extends Exception {
    public BackendErrorException(Exception e) {
        super(e.getMessage());
    }
}
