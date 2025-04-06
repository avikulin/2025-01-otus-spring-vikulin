package ru.otus.hw.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.stereotype.Service;
import ru.otus.hw.service.contracts.TestRunnerService;

/**
 * Возможно это избыточный класс, но видится, что правильно разделить ответственность
 * между запускатором приложения в целом и запускатором тестов. Типа SOLID и все такое....
 */
@Slf4j
@RequiredArgsConstructor
@Profile({"production","localized"})
@Command(group = "Student remaining knowledge testing")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppRunnerServiceImpl {
    TestRunnerService testRunnerService;

    @Command(description = "Run test", command = "run-test", alias = {"r", "run"})
    public void run() {
        log.info("Application started");
        this.testRunnerService.run();
        log.info("Application finished");
    }
}
