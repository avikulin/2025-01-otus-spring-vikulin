package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.service.contracts.StudentService;
import ru.otus.hw.service.io.contracts.IOService;
import ru.otus.hw.service.io.contracts.LocalizedIOService;
import ru.otus.hw.service.ioservice.config.LocalizedIoStubsConfig;
import ru.otus.hw.service.ioservice.stub.FakeStdIn;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Student determination behaviour check")
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:/test-application.yml")
@ContextConfiguration(classes = LocalizedIoStubsConfig.class)
class StudentServiceTest {
    private static final String STUDENT_NAME = "name";
    private static final String STUDENT_SURNAME = "surname";

    @Autowired
    @Qualifier("mockedIO")
    LocalizedIOService mockIOService;

    @Autowired
    FakeStdIn fakeStdIn;

    StudentService studentService;

    @BeforeEach
    public void setupTest(){
        this.studentService = new StudentServiceImpl(mockIOService);
        this.fakeStdIn.reset();
        this.fakeStdIn.writeContent(STUDENT_NAME + System.lineSeparator() +
                                    STUDENT_SURNAME + System.lineSeparator());
    }

    @Test
    @DisplayName("Get student's data from console input")
    void determineCurrentStudent() {
        var student = this.studentService.determineCurrentStudent();
        assertEquals(STUDENT_NAME, student.firstName());
        assertEquals(STUDENT_SURNAME, student.lastName());
    }
}