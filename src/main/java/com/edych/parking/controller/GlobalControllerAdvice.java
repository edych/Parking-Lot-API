package com.edych.parking.controller;

import com.edych.parking.exception.BadRequestException;
import com.edych.parking.exception.ConflictException;
import com.edych.parking.exception.NotFoundException;
import com.edych.parking.dto.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionDto> handleBadRequestException(BadRequestException exception, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ExceptionDto dto = getDto(status, exception.getMessage());

        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionDto> handleConflictException(ConflictException exception, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        ExceptionDto dto = getDto(status, exception.getMessage());

        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDto> handleNotFoundException(NotFoundException exception, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ExceptionDto dto = getDto(status, exception.getMessage());

        return new ResponseEntity<>(dto, status);
    }

    private ExceptionDto getDto(HttpStatus status, String message) {
        return ExceptionDto.builder()
                .message(message)
                .status(status.getReasonPhrase())
                .statusCode(status.value())
                .build();
    }
}
