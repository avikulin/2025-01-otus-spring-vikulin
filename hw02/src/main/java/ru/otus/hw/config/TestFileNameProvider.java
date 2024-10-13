package ru.otus.hw.config;

import org.springframework.stereotype.Component;

@Component
public interface TestFileNameProvider {
    String getTestFileName();
}
