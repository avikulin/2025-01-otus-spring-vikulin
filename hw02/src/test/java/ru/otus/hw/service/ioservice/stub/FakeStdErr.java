package ru.otus.hw.service.ioservice.stub;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Заглушка для System.out.print
 * Не поддерживает параллельное выполнение тестов!
 */
@Component
@Scope("singleton")
public class FakeStdErr implements DisposableBean {
    private final OutputStream fakeStdErr =  new ByteArrayOutputStream();
    private final PrintStream fakeErrorConsole =  new PrintStream(fakeStdErr);

    public PrintStream getInstance() {
        return this.fakeErrorConsole;
    }

    public String getContent() {
        return this.fakeStdErr.toString();
    }

    public void flush() {
        this.fakeErrorConsole.flush();
    }

    public void reset() {
        flush();
        ((ByteArrayOutputStream)this.fakeStdErr).reset();
    }

    @Override
    public void destroy() throws Exception {
        this.fakeStdErr.close();
        this.fakeErrorConsole.close();
    }
}
