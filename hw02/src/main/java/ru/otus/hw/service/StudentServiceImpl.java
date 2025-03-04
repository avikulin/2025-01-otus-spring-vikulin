package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Student;
import ru.otus.hw.service.contracts.StudentService;
import ru.otus.hw.service.io.contracts.IOService;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final IOService ioService;

    @Override
    public Student determineCurrentStudent() {
        var firstName = ioService.readStringWithPrompt("Please input your first name: ");
        var lastName = ioService.readStringWithPrompt("Please input your last name: ");
        return new Student(firstName, lastName);
    }
}
