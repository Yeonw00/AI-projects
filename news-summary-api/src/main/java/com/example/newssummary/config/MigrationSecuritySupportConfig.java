package com.example.newssummary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("payment-migrate")
public class MigrationSecuritySupportConfig {

    /**
     * payment-migrate 프로파일에서만 사용할 간단한 PasswordEncoder.
     * SecurityConfig는 @Profile("!payment-migrate")라 로딩되지 않기 때문에
     * 여기서만 최소한으로 PasswordEncoder 빈을 제공해 준다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}