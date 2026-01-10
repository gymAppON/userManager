package com.example.userManager.infrastructure.rabbitmq;

import com.example.userManager.domain.user.UserService;
import com.example.userManager.shared.exception.exceptions.general.CustomNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class RabbitMQListener {

    private final UserService userService;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
    }

    @RabbitListener(queues = "${rabbitmq.user_check_queue.name}")
    public boolean handleUserCheck(UUID userId) {
        boolean exist = true;
        try {
            userService.getById(userId);
        } catch (CustomNotFoundException e) {
            exist = false;
        }
        return exist;
    }
}
