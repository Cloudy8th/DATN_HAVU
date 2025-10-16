package com.project.hmartweb.application.constant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MailTemplate {
    public static final String ORDER = "order";
    public static final String SHIPPED = "shipped";
    public static final String FEEDBACK = "feedback";
}
