package com.project.hmartweb.config.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;

@Configuration
public class MailTemplateResolveConfig {
    private static final String PREFIX = "mailtemplates/";
    private static final String SUFFIX = ".html";
    private static final String CHARACTER_ENCODING = StandardCharsets.UTF_8.name();

    @Bean
    public ClassLoaderTemplateResolver mailTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix(PREFIX);
        templateResolver.setSuffix(SUFFIX);
        templateResolver.setCharacterEncoding(CHARACTER_ENCODING);
        templateResolver.setCacheable(true);
        templateResolver.setCheckExistence(true);
        return templateResolver;
    }
}
