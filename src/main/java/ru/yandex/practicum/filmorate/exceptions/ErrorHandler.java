package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e) {
        return new ErrorResponse("Ошибка валидации данных", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        return new ErrorResponse("Ресурс не найден", e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new ErrorResponse("Ошибка в теле запроса", e.getMessage());
    }

    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingPathVariableException(MissingPathVariableException e) {
        return new ErrorResponse("Отсутствует переменная пути: %s", e.getVariableName());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleMissingPathVariableException(Exception e) {
        return new ErrorResponse("Ошибка сервера",
                "Внутренняя ошибка сервера. Пожалуйста попробуйте позже");
    }

}

