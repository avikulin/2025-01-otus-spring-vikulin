package ru.otus.hw.mappers.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        componentModel = "spring"
)
public interface BaseConfig {}
