package com.project.tmartweb.application.services.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmailService implements IEmailService {
    private final JavaMailSender javaMailSender;
    @Value("${mail.username}")
    private String username;

    private final TemplateEngine templateEngine;

    @Override
    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            messageHelper.setSubject(subject);
            messageHelper.setTo(to);
            messageHelper.setText(content, true);
            messageHelper.setFrom(username);
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    @Async
    public void sendTemplateMail(String to, String subject, String templateName,
                                 Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        String content = templateEngine.process(templateName, context);

        sendEmail(to, subject, content);
    }
}
