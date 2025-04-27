package com.deye.web.exception.handler;

import com.deye.web.exception.*;
import com.deye.web.exception.response.DatabaseConstraintErrorResponseDto;
import com.deye.web.exception.response.ErrorResponseDto;
import com.deye.web.util.error.ErrorCodeUtils;
import io.jsonwebtoken.JwtException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(value = FileStorageException.class)
    public ResponseEntity<ErrorResponseDto> handleFileStorageException(FileStorageException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setCode(e.getCode());
        errorResponseDto.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = JsonException.class)
    public ResponseEntity<ErrorResponseDto> handleJsonException(JsonException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setCode(e.getCode());
        errorResponseDto.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = WrongRequestBodyException.class)
    public ResponseEntity<ErrorResponseDto> handleWrongRequestBodyException(WrongRequestBodyException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setCode(e.getCode());
        errorResponseDto.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ActionNotAllowedException.class)
    public ResponseEntity<ErrorResponseDto> handleActionNotAllowedException(ActionNotAllowedException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setCode(e.getCode());
        errorResponseDto.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setCode(e.getCode());
        errorResponseDto.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintDefinitionException(MethodArgumentNotValidException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setCode(ErrorCodeUtils.REQUEST_BODY_VALIDATION_ERROR_CODE);
        errorResponseDto.setMessage(errorMessagesConcat(e.getBindingResult().getAllErrors()));
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ErrorResponseDto errorResponseDto;
        if (ex.getCause() instanceof ConstraintViolationException constraintViolationException) {
            errorResponseDto = new DatabaseConstraintErrorResponseDto();
            errorResponseDto.setCode(ErrorCodeUtils.DATABASE_ERROR_CODE);
            errorResponseDto.setMessage(constraintViolationException.getSQLException().getMessage());
            ((DatabaseConstraintErrorResponseDto) errorResponseDto).setConstraintName(constraintViolationException.getConstraintName());
        } else {
            errorResponseDto = new ErrorResponseDto();
            errorResponseDto.setCode(ErrorCodeUtils.DATABASE_ERROR_CODE);
            errorResponseDto.setMessage(ex.getMessage());
        }

        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponseDto> handleJwtException(JwtException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setCode(ErrorCodeUtils.JWT_TOKEN_ERROR_CODE);
        errorResponseDto.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    private String errorMessagesConcat(List<ObjectError> errors) {
        Set<String> errorMessages = errors.stream()
                .filter(error -> error.getDefaultMessage() != null && !error.getDefaultMessage().isBlank())
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toSet());
        Optional<String> concatenatedMessages = errorMessages.stream()
                .reduce((a, b) -> a + "; " + b);
        return concatenatedMessages.orElse("error");
    }
}
