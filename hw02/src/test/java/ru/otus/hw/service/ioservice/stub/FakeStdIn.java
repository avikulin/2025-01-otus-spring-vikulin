package ru.otus.hw.service.ioservice.stub;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Component
public class FakeStdIn {
    private final byte[] content = new byte[1_000];
    private final InputStream fakeStdin =  new ByteArrayInputStream(content);

    public InputStream getInstance() {
        return this.fakeStdin;
    }

    public String getContent() {
        return Arrays.toString(this.content);
    }

    public void reset(){
        try {
            this.fakeStdin.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
