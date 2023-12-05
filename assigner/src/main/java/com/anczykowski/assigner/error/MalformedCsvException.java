package com.anczykowski.assigner.error;

public class MalformedCsvException extends RuntimeException {
    @SuppressWarnings("unused")
    public MalformedCsvException() {
        super();
    }

    public MalformedCsvException(String message) {
        super(message);
    }
}
