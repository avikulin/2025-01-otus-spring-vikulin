package ru.otus.hw.service.ioservice.stub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Заглушка для System.out.print
 * Не поддерживает параллельное выполнение тестов!
 */
public class FakeConsole {
    private final OutputStream fakeStdout =  new ByteArrayOutputStream();
    private final PrintStream fakeConsole =  new PrintStream(fakeStdout);

    public PrintStream getInstance() {
        return fakeConsole;
    }

    public String getContent() {
        return this.fakeStdout.toString();
    }

    public void flush() {
        this.fakeConsole.flush();
    }

    public void reset() {
        flush();
        ((ByteArrayOutputStream)this.fakeStdout).reset();
    }

    public void close(){
        try {
            this.fakeStdout.close();
            this.fakeConsole.close();
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during the closing of the streams: "+e.getLocalizedMessage());
        }
    }
}
