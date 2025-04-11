package ru.otus.hw.service.ioservice.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.OpenCsvConfiguration;
import ru.otus.hw.config.TestServiceConfiguration;
import ru.otus.hw.service.io.LocalizedStreamsIOService;
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

import java.util.Locale;


@Configuration
@Import({FakeStdErr.class, FakeStdIn.class, FakeStdOut.class, AppProperties.class})
@EnableConfigurationProperties({TestServiceConfiguration.class,  OpenCsvConfiguration.class})
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
    AppProperties appConfig;

    @Bean("mockedLocalizedIO")
    public LocalizedIOService getMockedIO(LocalizedInputFormatter localizedInputFormatter,
                                          LocalizedInputValidator inputValidator,
                                          LocalizedMessagesService messagesService) {
        return new LocalizedStreamsIOService(fakeStdOut.getInstance(),
                                             fakeStdErr.getInstance(),
                                             fakeStdIn.getInstance(),
                                             localizedInputFormatter,
                                             inputValidator,
                                             appConfig,
                                             messagesService
        );
    }

    @Bean
    public MessageSource messageSource() {
        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(RESOURCE_BUNDLE_ID);
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setDefaultLocale(Locale.US);
        return messageSource;
    }

    @Bean
    public LocalizedMessagesService getLocalizedMessagesService(MessageSource messageSource) {
        return new LocalizedMessagesServiceImpl(this.appConfig.getTestServiceConfiguration(), messageSource);
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
