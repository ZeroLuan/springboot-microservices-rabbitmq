package com.ms.email.controller;

import com.ms.email.dto.EmailResponseDTO;
import com.ms.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<List<EmailResponseDTO>> getAllEmails() {
        return ResponseEntity.ok(emailService.findAll());
    }

    @GetMapping("/{emailId}")
    public ResponseEntity<EmailResponseDTO> getEmailById(@PathVariable UUID emailId) {
        return ResponseEntity.ok(emailService.findById(emailId));
    }
}

