package com.example.userManager.infrastructure.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import java.util.List;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.user_check_queue.name}")
    private String userQueue;

    public static final String RPC_EXCHANGE_NAME = "internal-exchange";

    // 1. Основная очередь сообщений
    @Bean
    public Queue queue() {
        // true, чтобы сообщения не терялись при рестарте брокера
        return new Queue(queueName, true);
    }

    // 2. Очередь для RPC запросов
    @Bean
    public Queue userCheckQueue() {
        return new Queue(userQueue, true);
    }

    // 3. Exchange для RPC (обязателен, так как ты шлешь через него)
    @Bean
    public DirectExchange internalExchange() {
        return new DirectExchange(RPC_EXCHANGE_NAME);
    }

    // 4. Связь (Binding) - чтобы сообщение попало из Exchange в Очередь
    @Bean
    public Binding bindingUserCheck() {
        return BindingBuilder.bind(userCheckQueue())
                .to(internalExchange())
                .with(userQueue);
    }

    @Bean
    public MessageConverter messageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();

        converter.setAllowedListPatterns(List.of(
                "java.util.*",
                "java.lang.*",
                "com.example.*"
        ));

        return converter;
    }
}
