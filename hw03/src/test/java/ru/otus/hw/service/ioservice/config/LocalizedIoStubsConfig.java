package ru.otus.hw.service.ioservice.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import ru.otus.hw.config.LocalizationServiceConfiguration;
import ru.otus.hw.config.TestServicePropertiesProvider;
import ru.otus.hw.config.contracts.LocaleConfig;
import ru.otus.hw.config.contracts.TestPropertiesProvider;
import ru.otus.hw.service.io.LocalizedStreamsIOService;
import ru.otus.hw.service.io.contracts.IOService;
import ru.otus.hw.service.io.contracts.LocalizedIOService;
import ru.otus.hw.service.ioservice.stub.FakeStdErr;
import ru.otus.hw.service.ioservice.stub.FakeStdIn;
import ru.otus.hw.service.ioservice.stub.FakeStdOut;
import ru.otus.hw.service.localization.LocalizedMessagesServiceImpl;
import ru.otus.hw.service.localization.contracts.LocalizedMessagesService;
import ru.otus.hw.utils.formatters.localized.LocalizedInputStreamFormatterImpl;
import ru.otus.hw.utils.formatters.localized.contracts.LocalizedInputFormatter;
import ru.otus.hw.utils.validators.localized.LocalizedInputValidatorImpl;
import ru.otus.hw.utils.validators.localized.contracts.LocalizedInputValidator;


@Configuration
@Import({FakeStdErr.class, FakeStdIn.class, FakeStdOut.class})
@EnableConfigurationProperties({TestServicePropertiesProvider.class,  LocalizationServiceConfiguration.class})
@Profile({"test","localized"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocalizedIoStubsConfig {
    static String RESOURCE_BUNDLE_ID = "messages";

    @Autowired
    FakeStdOut fakeStdOut;

    @Autowired
    FakeStdErr fakeStdErr;

    @Autowired
    FakeStdIn fakeStdIn;

    @Autowired
    TestPropertiesProvider testPropertiesProvider;

    @Autowired
    LocaleConfig localeConfig;

    @Bean("mockedLocalizedIO")
    public LocalizedIOService getMockedIO(LocalizedInputFormatter localizedInputFormatter,
                                          LocalizedInputValidator inputValidator,
                                          LocalizedMessagesService messagesService) {
        return new LocalizedStreamsIOService(fakeStdOut.getInstance(),
                                             fakeStdErr.getInstance(),
                                             fakeStdIn.getInstance(),
                                             localizedInputFormatter,
                                             inputValidator,
                                             testPropertiesProvider,
                                             messagesService
        );
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(RESOURCE_BUNDLE_ID); // Load messages.properties
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocalizedMessagesService getLocalizedMessagesService(MessageSource messageSource) {
        return new LocalizedMessagesServiceImpl(this.localeConfig, messageSource);
    }

    @Bean
    public LocalizedInputFormatter getLocalizedInputFormatter(LocalizedMessagesService messagesService) {
        return new LocalizedInputStreamFormatterImpl(messagesService);
    }

    @Bean
    public LocalizedInputValidator getLocalizedInputValidator(LocalizedMessagesService messagesService) {
        return new LocalizedInputValidatorImpl(messagesService);
    }
}
