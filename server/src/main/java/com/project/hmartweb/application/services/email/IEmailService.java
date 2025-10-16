package com.project.hmartweb.application.services.email;

import jakarta.mail.MessagingException;

import java.util.Map;

public interface IEmailService {
    void sendEmail(String to, String subject, String email) throws MessagingException;

    void sendTemplateMail(String to, String subject, String templateName, Map<String, Object> variables);
}
