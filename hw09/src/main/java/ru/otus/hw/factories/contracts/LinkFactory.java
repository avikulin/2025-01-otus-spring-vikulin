package ru.otus.hw.factories.contracts;

import ru.otus.hw.dto.navigation.NavigationItemDto;
import ru.otus.hw.factories.metadata.LinkKey;

public interface LinkFactory {
    NavigationItemDto getUndirectedLink(LinkKey linkKey);

    NavigationItemDto getDirectedLink(LinkKey linkKey, String linkTitle, long targetPathToken);

    NavigationItemDto getDirectedLink(LinkKey linkKey, String linkTitle, long... targetPathTokens);
}
