package com.example.genesisclub.genesisClub.Configuracion;

import java.util.Properties;

import org.springframework.lang.NonNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource("classpath:email.properties")
public class EmailConfig {

    @Value("${email.username}")
    private String email;

    @Value("${email.password}")
    private String password;


    private @NonNull Properties getMailProperties() {
    Properties properties = new Properties();

    properties.put("mail.smtp.auth", "true");
    // Corregido: starttls.enable
    properties.put("mail.smtp.starttls.enable", "true"); 
    // Corregido: host (smtp, no smpt)
    properties.put("mail.smtp.host", "smtp.gmail.com");
    // Corregido: port (smtp, no smpt y un solo punto)
    properties.put("mail.smtp.port", "587");

    return properties;
}


    @Bean
public JavaMailSender javaMailSender() {
    JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
    
    // Configuración principal obligatoria
    mailSenderImpl.setHost("smtp.gmail.com");
    mailSenderImpl.setPort(587);
    mailSenderImpl.setUsername(email);
    mailSenderImpl.setPassword(password);
    
    // Configuración adicional de seguridad
    mailSenderImpl.setJavaMailProperties(getMailProperties());

    return mailSenderImpl;
}
    
    @Bean
    public ResourceLoader resourceLoader()
    {
        return new DefaultResourceLoader();
    }
}
