package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ru.otus.hw.base.ConfigurableByPropertiesTestBase;
import ru.otus.hw.service.contracts.StudentService;
import ru.otus.hw.service.io.contracts.LocalizedIOService;
import ru.otus.hw.service.ioservice.config.LocalizedIoStubsConfig;
import ru.otus.hw.service.ioservice.stub.FakeStdIn;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = LocalizedIoStubsConfig.class)
@DisplayName("Student determination behaviour check")
@TestPropertySource(properties = "test.locale=en-US")
@ActiveProfiles(profiles = "localized")
class StudentServiceTest  extends ConfigurableByPropertiesTestBase {
    private static final String STUDENT_NAME = "name";
    private static final String STUDENT_SURNAME = "surname";

    @Autowired
    @Qualifier("mockedLocalizedIO")
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