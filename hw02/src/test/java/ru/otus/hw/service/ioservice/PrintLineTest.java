package ru.otus.hw.service.ioservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.service.io.IOService;
import ru.otus.hw.service.ioservice.stub.FakeStdErr;
import ru.otus.hw.service.ioservice.stub.FakeStdOut;
import ru.otus.hw.service.ioservice.config.StubCfgInitializer;
import ru.otus.hw.service.ioservice.utils.Normalizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {StubCfgInitializer.class})
class PrintLineTest {
    @Qualifier("getMockedIO")
    @Autowired
    private IOService ioService;

    @Autowired
    private FakeStdOut fakeStdOut;

    @Autowired
    private FakeStdErr fakeStdErr;

    @BeforeEach
    public void setupMocks(){
        this.fakeStdOut.reset();
        this.fakeStdErr.reset();
    }

    @ParameterizedTest(name = "{index}. {0}")
    @CsvFileSource(resources = "/formatter-tests/test-data-source.csv",delimiter = ';',numLinesToSkip = 1)
    void printFormattedContentTest(String nameOfTheTest, String valuePassed, String valueExpected){
        assertNotNull(this.ioService);
        assertNotNull(this.fakeStdOut);
        assertNotNull(this.fakeStdErr);
        this.ioService.printLine(valuePassed);
        this.fakeStdOut.flush();
        this.fakeStdErr.flush();
        assertEquals(Normalizer.newLine(valueExpected), this.fakeStdOut.getContent());
        assertEquals("", this.fakeStdErr.getContent());
    }
}