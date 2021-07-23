package com.leijendary.spring.iamtemplate.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.module.jsr310.Jsr310Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static org.modelmapper.module.jsr310.Jsr310ModuleConfig.builder;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        final var config = builder()
                .dateTimeFormatter(ISO_INSTANT)
                .build();
        final var modelMapper = new ModelMapper();
        modelMapper.registerModule(new Jsr310Module(config));

        return modelMapper;
    }
}
