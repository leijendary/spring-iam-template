package com.leijendary.spring.iamtemplate.event.producer;

import com.leijendary.spring.iamtemplate.event.schema.NotificationSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

import static reactor.core.publisher.Sinks.many;

@Component
public class NotificationProducer extends AbstractProducer<NotificationSchema> {

    private final Sinks.Many<Message<NotificationSchema>> smsBuffer = many().multicast().onBackpressureBuffer();
    private final Sinks.Many<Message<NotificationSchema>> emailBuffer = many().multicast().onBackpressureBuffer();

    @Bean
    public Supplier<Flux<Message<NotificationSchema>>> notificationSms() {
        return smsBuffer::asFlux;
    }

    @Bean
    public Supplier<Flux<Message<NotificationSchema>>> notificationEmail() {
        return emailBuffer::asFlux;
    }

    public void sms(final NotificationSchema notificationSchema) {
        final var message = message(notificationSchema);

        smsBuffer.tryEmitNext(message);
    }

    public void email(final NotificationSchema notificationSchema) {
        final var message = message(notificationSchema);

        emailBuffer.tryEmitNext(message);
    }
}
