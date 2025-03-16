package ru.otus.hw.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Student;
import ru.otus.hw.service.contracts.StudentService;
import ru.otus.hw.service.io.contracts.LocalizedIOService;

@Service
@Profile("localized")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentServiceImpl implements StudentService {
    static String MSG_CODE_FIRST_NAME_PROMPT="student-service.msg.prompt.first-name";

    static String MSG_CODE_LAST_NAME_PROMPT="student-service.msg.prompt.last-name";

    LocalizedIOService localizedIoService;

    @Override
    public Student determineCurrentStudent() {
        var firstName = localizedIoService.readStringWithPromptLocalized(MSG_CODE_FIRST_NAME_PROMPT);
        var lastName = localizedIoService.readStringWithPromptLocalized(MSG_CODE_LAST_NAME_PROMPT);
        return new Student(firstName, lastName);
    }
}
