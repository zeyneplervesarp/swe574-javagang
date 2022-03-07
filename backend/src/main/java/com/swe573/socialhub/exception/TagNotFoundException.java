package com.swe573.socialhub.exception;

public class TagNotFoundException extends RuntimeException {

    public TagNotFoundException(Long id) {
        super("Could not find tag " + id);
    }
}