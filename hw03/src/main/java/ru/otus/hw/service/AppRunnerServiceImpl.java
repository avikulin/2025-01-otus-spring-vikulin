package ru.otus.hw.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.otus.hw.service.contracts.TestRunnerService;

/**
 * Возможно это избыточный класс, но видится, что правильно разделить ответственность
 * между запускатором приложения в целом и запускатором тестов. Типа SOLID и все такое....
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile("localized")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppRunnerServiceImpl implements CommandLineRunner {
    TestRunnerService testRunnerService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Application started");
        this.testRunnerService.run();
        log.info("Application finished");
    }
}
