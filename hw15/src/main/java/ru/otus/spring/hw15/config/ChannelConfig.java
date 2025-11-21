package ru.otus.spring.hw15.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;

import static ru.otus.spring.hw15.config.Constants.INPUT_MSG_CHANNEL;

@Configuration
public class ChannelConfig {
    @Bean
    public MessageChannel getInputChannel(){
        return MessageChannels.queue(INPUT_MSG_CHANNEL, 100).getObject();
    }
}
