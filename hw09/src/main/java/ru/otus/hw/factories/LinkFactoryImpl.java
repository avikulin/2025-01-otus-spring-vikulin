package ru.otus.hw.factories;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.message.Message;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.ArrayUtils;
import ru.otus.hw.dto.navigation.NavigationItemDto;
import ru.otus.hw.factories.contracts.LinkFactory;
import ru.otus.hw.factories.metadata.LinkKey;

import java.text.MessageFormat;
import java.util.Arrays;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LinkFactoryImpl implements LinkFactory {
    static String PATH_SEPARATOR = "/";

    @Override
    public NavigationItemDto getUndirectedLink(LinkKey linkKey) {
        return new NavigationItemDto(linkKey.getTitle(), linkKey.getViewPath(), linkKey.getUrl());
    }

    @Override
    public NavigationItemDto getDirectedLink(LinkKey linkKey, String linkTitle, long targetPathToken) {
        return this.getDirectedLink(linkKey, linkTitle, new long[]{targetPathToken});
    }

    @Override
    public NavigationItemDto getDirectedLink(LinkKey linkKey, String linkTitle, long... targetPathTokens) {
        var pathParams = Arrays.stream(targetPathTokens).boxed().toArray(); // функционально избыточно, но я за type-checking
        var directedUrl = MessageFormat.format(linkKey.getUrl(), pathParams);
        return new NavigationItemDto(linkTitle, linkKey.getViewPath(), directedUrl);
    }
}
