package com.ms.email.service;

import com.ms.email.dto.EmailResponseDTO;
import com.ms.email.entity.EmailModel;
import com.ms.email.enums.StatusEmail;
import com.ms.email.exception.EntityNotFoundException;
import com.ms.email.mapper.EmailMapper;
import com.ms.email.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;
    private final JavaMailSender emailSender;
    private final EmailMapper emailMapper;

    @Value(value = "${spring.mail.username}")
    private String emailFrom;

    @Transactional
    public EmailModel sendEmail(EmailModel emailModel) {
        try {
            emailModel.setSendDateEmail(LocalDateTime.now());
            emailModel.setEmailFrom(emailFrom);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailModel.getEmailFrom());
            message.setTo(emailModel.getEmailTo());
            message.setSubject(emailModel.getSubject());
            message.setText(emailModel.getText());
            emailSender.send(message);

            emailModel.setStatusEmail(StatusEmail.SENT);
            log.info("E-mail enviado com sucesso para: {}", emailModel.getEmailTo());
        } catch (MailException e) {
            emailModel.setStatusEmail(StatusEmail.ERROR);
            log.error("Erro ao enviar e-mail para {}: {}", emailModel.getEmailTo(), e.getMessage());
        }

        return emailRepository.save(emailModel);
    }

    @Transactional(readOnly = true)
    public List<EmailResponseDTO> findAll() {
        return emailMapper.toResponseDTOList(emailRepository.findAll());
    }

    @Transactional(readOnly = true)
    public EmailResponseDTO findById(UUID emailId) {
        EmailModel email = emailRepository.findById(emailId)
                .orElseThrow(() -> new EntityNotFoundException("E-mail não encontrado."));
        return emailMapper.toResponseDTO(email);
    }
}

