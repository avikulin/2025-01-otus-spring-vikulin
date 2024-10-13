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
public class FakeStdOut {
    private final OutputStream fakeStdOut =  new ByteArrayOutputStream();
    private final PrintStream fakePrintConsole =  new PrintStream(fakeStdOut);

    public PrintStream getInstance() {
        return fakePrintConsole;
    }

    public String getContent() {
        return this.fakeStdOut.toString();
    }

    public void flush() {
        this.fakePrintConsole.flush();
    }

    public void reset() {
        flush();
        ((ByteArrayOutputStream)this.fakeStdOut).reset();
    }

    public void close(){
        try {
            this.fakeStdOut.close();
            this.fakePrintConsole.close();
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during the closing of the streams: "+e.getLocalizedMessage());
        }
    }
}
