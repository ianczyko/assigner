package com.anczykowski.assigner.error;

public class NotFoundException extends RuntimeException {
    @SuppressWarnings("unused")
    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }
}
