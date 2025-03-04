package ru.otus.hw.dao;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"ru.otus.hw.dao", "ru.otus.hw.config"})
public class DaoContextConfiguration {}
