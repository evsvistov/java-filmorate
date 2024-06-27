package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
@Slf4j
public class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
        log.error("Ошибка сервера", message);
    }
}
