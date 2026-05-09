package org.lupoi.workoutapp.infrastructure.service;/*
    @author Andrii
    @project workout
    @class EmailService
    @version 1.0.0
    @since 09.05.2026 - 12.09
*/

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendPasswordResetEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("WorkoutApp — Скидання пароля");
        message.setText("""
                Привіт!
                
                Ти запросив скидання пароля для свого акаунту WorkoutApp.
                
                Перейди за посиланням щоб встановити новий пароль:
                %s
                
                Посилання дійсне 1 годину.
                
                Якщо ти не робив цей запит — просто ігноруй цей лист.
                """.formatted(resetLink));
        mailSender.send(message);
    }
}
