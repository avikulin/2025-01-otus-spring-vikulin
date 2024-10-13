package ru.otus.hw.service.ioservice.stub;

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
public class FakeStdErr {
    private final OutputStream fakeStdErr =  new ByteArrayOutputStream();
    private final PrintStream fakeErrorConsole =  new PrintStream(fakeStdErr);

    public PrintStream getInstance() {
        return fakeErrorConsole;
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

    public void close(){
        try {
            this.fakeStdErr.close();
            this.fakeErrorConsole.close();
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during the closing of the streams: "+e.getLocalizedMessage());
        }
    }
}
