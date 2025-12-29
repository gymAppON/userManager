package com.example.userManager.infrastructure.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RabbitMQListener {

    private final static Random RANDOM = new Random();

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
    }

    @RabbitListener(queues = "${rabbitmq.queue_with_delay.name}")
    public void receiveMessageWithDelay(String message) throws InterruptedException {
        var delay = RANDOM.nextInt(10000);
        Thread.sleep(delay);
        System.out.println("Received message: " + message + " with delay: " + delay);
    }

}
