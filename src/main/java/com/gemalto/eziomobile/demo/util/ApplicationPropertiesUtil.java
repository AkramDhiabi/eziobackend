package com.gemalto.eziomobile.demo.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@ComponentScan(basePackages = { "com.gemalto.*" })
@PropertySource("classpath:application.properties")
@Component
@Getter
public class ApplicationPropertiesUtil {
    @Value("${smtp.email}")
    private String smtpEmail;

    @Value("${smtp.password}")
    private String smtpPassword;

    @Value("${smtp.port}")
    private String smtpPort;

    @Value("${smtp.host}")
    private String smtpHost;

    @Value("${smtp.is.auth}")
    private String smtpIsAuth;

    @Value("${smtp.starttls}")
    private String smtStartTlsIsEnable;

    @Value("${messenger.channel}")
    private String channel;

    @Value("${messenger.applications.id}")
    private String applicationsID;

}
