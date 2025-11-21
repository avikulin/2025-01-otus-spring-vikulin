package ru.otus.spring.hw15.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.spring.hw15.repo.AccountsRepo;

@ShellComponent
public class ShellCmd {
    AccountsRepo accountsRepo;

    public ShellCmd(AccountsRepo accountsRepo) {
        this.accountsRepo = accountsRepo;
    }

    @ShellMethod("run")
    public String run() {
        StringBuilder result = new StringBuilder();
        this.accountsRepo.findAll().forEach(
                a->result.append("%d) %s:% ,10.2f".formatted(
                                                                    a.getAccountId(),
                                                                    a.getHolderName(),
                                                                    a.getBalance()
                                                                 )
                )
        );
        return result.toString();
    }
}
