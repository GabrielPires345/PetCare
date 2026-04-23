package com.petcare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Async
    public void enviarEmailVerificacao(String destinatario, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("PetCare - Verifique seu email");
        message.setText(
                "Olá! Obrigado por se cadastrar no PetCare.\n\n" +
                "Para verificar seu email, clique no link abaixo:\n" +
                baseUrl + "/api/auth/verificar-email?token=" + token + "\n\n" +
                "Se você não se cadastrou, ignore este email."
        );
        mailSender.send(message);
    }
}
