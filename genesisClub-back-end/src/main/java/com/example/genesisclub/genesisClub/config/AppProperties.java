package com.example.genesisclub.genesisClub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotEmpty;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @NotEmpty
    private String frontUrl;

    public String getFrontUrl() {
        return frontUrl;
    }

    public void setFrontUrl(String frontUrl) {
        this.frontUrl = frontUrl;
    }
}
