package ru.otus.hw.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.hw.factories.contracts.NavigationFactory;

import static ru.otus.hw.controllers.Constants.MODEL_KEY_NAVIGATION_STATE;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class HomeController {
    NavigationFactory navFactory;
    @GetMapping("/")
    public String home(Model model){
        model.addAttribute(MODEL_KEY_NAVIGATION_STATE, navFactory.getHomeRootNav());
        return "public/index";
    }
}
