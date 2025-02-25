package ru.otus.hw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public interface TestFileNameProvider {
    String getTestFileName();
}
