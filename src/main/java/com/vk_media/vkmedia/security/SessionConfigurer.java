/*package com.vk_media.vkmedia.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

public class SessionConfigurer extends AbstractHttpConfigurer<SessionConfigurer, HttpSecurity> {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
    }

    public static SessionConfigurer oauthHttpConfigurer() {
        return new SessionConfigurer();
    }
}
*/