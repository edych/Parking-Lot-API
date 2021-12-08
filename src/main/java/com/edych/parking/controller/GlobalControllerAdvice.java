package com.edych.parking.controller;

import com.edych.parking.dto.ExceptionDto;
import com.edych.parking.exception.BadRequestException;
import com.edych.parking.exception.ConflictException;
import com.edych.parking.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionDto> handleBadRequestException(final BadRequestException exception, final WebRequest request) {
        final HttpStatus status = HttpStatus.BAD_REQUEST;

        final ExceptionDto dto = getDto(status, exception.getMessage());

        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionDto> handleConflictException(final ConflictException exception, final WebRequest request) {
        final HttpStatus status = HttpStatus.CONFLICT;

        final ExceptionDto dto = getDto(status, exception.getMessage());

        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDto> handleNotFoundException(final NotFoundException exception, final WebRequest request) {
        final HttpStatus status = HttpStatus.NOT_FOUND;

        final ExceptionDto dto = getDto(status, exception.getMessage());

        return new ResponseEntity<>(dto, status);
    }

    private ExceptionDto getDto(final HttpStatus status, final String message) {
        return ExceptionDto.builder()
                .message(message)
                .status(status.getReasonPhrase())
                .statusCode(status.value())
                .build();
    }
}
