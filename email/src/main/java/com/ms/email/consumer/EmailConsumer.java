package com.ms.email.consumer;

import com.ms.email.dto.EmailMessageDTO;
import com.ms.email.entity.EmailModel;
import com.ms.email.mapper.EmailMapper;
import com.ms.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;
    private final EmailMapper emailMapper;

    @RabbitListener(queues = "${broker.queue.email.name}")
    public void listenEmailQueue(@Payload EmailMessageDTO emailMessageDTO) {
        log.info("Mensagem recebida da fila para envio de e-mail ao destinatário: {}", emailMessageDTO.emailTo());
        EmailModel emailModel = emailMapper.toEntity(emailMessageDTO);
        emailService.sendEmail(emailModel);
    }
}

