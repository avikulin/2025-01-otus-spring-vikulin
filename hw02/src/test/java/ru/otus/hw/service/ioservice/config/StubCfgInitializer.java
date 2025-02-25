package ru.otus.hw.service.ioservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw.service.io.IOService;
import ru.otus.hw.service.io.StreamsIOService;
import ru.otus.hw.service.ioservice.stub.FakeStdErr;
import ru.otus.hw.service.ioservice.stub.FakeStdIn;
import ru.otus.hw.service.ioservice.stub.FakeStdOut;


@Configuration
@ComponentScan(basePackages = {"ru.otus.hw.service.ioservice.stub"})
public class StubCfgInitializer {
    @Autowired
    private FakeStdOut fakeStdOut;

    @Autowired
    private FakeStdErr fakeStdErr;

    @Autowired
    private FakeStdIn fakeStdIn;

    @Bean
    public IOService getMockedIO(){
        return new StreamsIOService(fakeStdOut.getInstance(),
                                    fakeStdErr.getInstance(),
                                    fakeStdIn.getInstance());
    }
}
