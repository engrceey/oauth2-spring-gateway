package com.zurum.commonlibs.exceptions;


import com.zurum.commonlibs.dtos.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolation;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
//@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException exception) {
        log.error("{}",exception.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .isSuccessful(false)
                .statusMessage(exception.getMessage())
                .build();

        return ResponseEntity.status(exception.getStatus()).body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleMaxSizeException(MaxUploadSizeExceededException exc) {

        ApiResponse<Object> response = ApiResponse.builder()
                .isSuccessful(false)
                .statusMessage("An error occurred, check message below")
                .data("File too large!")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {

        ApiResponse<Object> response = ApiResponse.builder()
                .isSuccessful(false)
                .statusMessage(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({ResourceCreationException.class})
    public ResponseEntity<ApiResponse<Object>> resourceConflictException(Exception ex, WebRequest request) {

        ApiResponse<Object> response = ApiResponse.builder()
                .isSuccessful(false)
                .statusMessage(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> globalExceptionHandler(Exception ex, WebRequest request) {

        ApiResponse<Object> response = ApiResponse.builder()
                .isSuccessful(false)
                .statusMessage(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }

    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(final javax.validation.ConstraintViolationException exception) {
        final Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        final String errors =  violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(""));

        ApiResponse<Object> response = ApiResponse.builder()
                .isSuccessful(false)
                .statusMessage(errors)
                .build();

        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Object>> handleGlobalExceptions(MethodArgumentNotValidException ex,
                                                                         WebRequest request) {
        String[] errors = ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);

        ApiResponse<Object> response = ApiResponse.builder()
                .isSuccessful(false)
                .statusMessage("An error occurred, check message below")
                .data(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                      WebRequest request) {
        var apiError = new ApiResponse<>();
        apiError.setIsSuccessful(false);
        apiError.setStatusMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(), ex.getValue(),
                Objects.requireNonNull(ex.getRequiredType()).getSimpleName()));
        apiError.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

}