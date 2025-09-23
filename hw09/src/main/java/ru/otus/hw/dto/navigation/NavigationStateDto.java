package ru.otus.hw.dto.navigation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NavigationStateDto {

    List<NavigationItemDto> breadCrumbState;

    List<NavigationItemDto> leftSideBarState;

    public String getTemplatePath(){
        if (CollectionUtils.isEmpty(breadCrumbState)){
            throw new IllegalStateException("Operation is unsupported on the undefined navigation state");
        }
        var lastIdx = this.breadCrumbState.size() - 1;
        var current = this.breadCrumbState.get(lastIdx);
        return current.getTemplatePath();
    }
}
