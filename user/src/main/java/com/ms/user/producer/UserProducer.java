package com.ms.user.producer;

import com.ms.user.dto.EmailMessageDTO;
import com.ms.user.entity.UserModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value(value = "${broker.queue.email.name}")
    private String routingKey;

    public void publishMessageEmail(UserModel userModel) {
        var emailMessageDTO = new EmailMessageDTO(
                userModel.getUserId(),
                userModel.getEmail(),
                "Bem-vindo(a) ao nosso serviço!",
                "Olá " + userModel.getName() + ",\n\nObrigado pelo seu cadastro! Seu acesso foi criado com sucesso em nossa plataforma."
        );

        rabbitTemplate.convertAndSend("", routingKey, emailMessageDTO);
        log.info("Evento de envio de e-mail publicado na fila '{}' para o usuário {}", routingKey, userModel.getUserId());
    }
}

