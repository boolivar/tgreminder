package org.bool.tgreminder.config;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("nitrite")
public class NitriteConfig {
    
    private String username;
    
    private String password;
    
    @Bean
    public Nitrite database() {
        if (username != null && password != null) {
            return nitrite().openOrCreate(username, password);
        }
        return nitrite().openOrCreate();
    }
    
    @Bean
    @ConfigurationProperties("nitrite.config")
    public NitriteBuilder nitrite() {
        return Nitrite.builder();
    }
}
