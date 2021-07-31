package com.leijendary.spring.iamtemplate.event.producer;

import com.leijendary.spring.iamtemplate.event.schema.UserSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

import static reactor.core.publisher.Sinks.many;

@Component
public class UserProducer extends AbstractProducer<UserSchema> {

    private final Sinks.Many<Message<UserSchema>> createBuffer = many().multicast().onBackpressureBuffer();
    private final Sinks.Many<Message<UserSchema>> updateBuffer = many().multicast().onBackpressureBuffer();
    private final Sinks.Many<Message<UserSchema>> deactivateBuffer = many().multicast().onBackpressureBuffer();

    @Bean
    public Supplier<Flux<Message<UserSchema>>> userCreate() {
        return createBuffer::asFlux;
    }

    @Bean
    public Supplier<Flux<Message<UserSchema>>> userUpdate() {
        return updateBuffer::asFlux;
    }

    @Bean
    public Supplier<Flux<Message<UserSchema>>> userDeactivate() {
        return deactivateBuffer::asFlux;
    }

    public void create(final UserSchema userSchema) {
        final var message = messageWithKey(String.valueOf(userSchema.getId()), userSchema);

        createBuffer.tryEmitNext(message);
    }

    public void update(final UserSchema userSchema) {
        final var message = messageWithKey(String.valueOf(userSchema.getId()), userSchema);

        updateBuffer.tryEmitNext(message);
    }

    public void deactivate(final UserSchema userSchema) {
        final var message = messageWithKey(String.valueOf(userSchema.getId()), userSchema);

        deactivateBuffer.tryEmitNext(message);
    }
}
