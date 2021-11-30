package com.edych.parking.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String resourceName, Long id) {
        super(String.format("Resource [%s] with id [%s] does not exist.", resourceName, id));
    }
}
