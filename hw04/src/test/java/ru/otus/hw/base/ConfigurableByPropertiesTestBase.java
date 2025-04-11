package ru.otus.hw.base;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"spring.config.name=test-application",
                                  "spring.config.location=classpath:/"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // избыточно, но на тонкую настройку
                                                                             // нет ни времени, ни желания...
@ActiveProfiles(profiles = {"test"})
public class ConfigurableByPropertiesTestBase {}
