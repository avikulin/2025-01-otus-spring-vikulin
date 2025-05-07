package ru.otus.hw.utils.factories.exceptions;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.otus.hw.utils.factories.exceptions.contracts.LoggedExceptionFactory;

@Slf4j
@Component
public class LoggedExceptionFactoryImpl implements LoggedExceptionFactory {
    @Override
    public void logAndThrow(String text, Class<? extends RuntimeException> exception) {
        logAndThrow(text, null, exception, null);
    }

    @Override
    public void logAndThrow(String text, Class<? extends RuntimeException> exception, Throwable cause) {
        logAndThrow(text, null, exception, cause);
    }

    @Override
    public void logAndThrow(String text, Long id, Class<? extends RuntimeException> exception) {
        logAndThrow(text, id, exception, null);
    }

    @Override
    @SneakyThrows //вынужденная мера, чтобы не городить огород
    public void logAndThrow(String text, Long id, Class<? extends RuntimeException> exception, Throwable cause) {
        var msg = id == null ? text : text.formatted(id);
        log.error(msg, cause);
        if (cause == null) {
            var constructor = exception.getConstructor(String.class);
            throw constructor.newInstance(msg);
        } else {
            var constructorWithCause = exception.getConstructor(String.class, Throwable.class);
            throw constructorWithCause.newInstance(msg, cause);
        }
    }
}
