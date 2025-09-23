package ru.otus.hw.base;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"spring.config.name=application-test",
                                  "spring.config.location=classpath:/"})
public class ConfigurableByPropertiesTestBase {}
