package ru.otus.hw.service.runner;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.service.TestService;

@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    @Override
    public void run() {
        testService.executeTest();
    }
}
