package ru.otus.hw.service.ioservice.stub;

import lombok.SneakyThrows;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Component
@Scope("singleton")
public class FakeStdIn implements DisposableBean {
    private static final int MAX_CONTENT_LENGTH = 1024;
    private final byte[] content = new byte[MAX_CONTENT_LENGTH];
    private final InputStream fakeStdin = new ByteArrayInputStream(this.content);

    public InputStream getInstance() {
        return this.fakeStdin;
    }

    @SneakyThrows
    public void writeContent(String content) {
        Validate.notBlank(content);
        try(var writer = new ByteArrayOutputStream()) {
            writer.write(content.getBytes());
            var data = writer.toByteArray();
            System.arraycopy(data, 0, this.content, 0, data.length);
        }
    }

    @SneakyThrows
    public void reset(){
        this.fakeStdin.reset();
    }

    @Override
    public void destroy() throws Exception {
        this.fakeStdin.close();
    }
}
