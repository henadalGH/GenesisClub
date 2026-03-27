package com.example.genesisclub.genesisClub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Valor en ms. Se puede personalizar con jwt.expiration-time en application-*.properties.
     */
    private long expirationTime = 900000L;

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
