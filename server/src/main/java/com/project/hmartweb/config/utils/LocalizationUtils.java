package com.project.hmartweb.config.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class LocalizationUtils {
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public String getLocalizeMessage(String message, Objects... params) {
        HttpServletRequest request = WebUtils.getCurrentRequest();
        Locale locale = localeResolver.resolveLocale(request);
        return messageSource.getMessage(message, params, locale);
    }
}
