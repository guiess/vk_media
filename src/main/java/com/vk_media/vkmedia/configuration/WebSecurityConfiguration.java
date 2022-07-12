package com.vk_media.vkmedia.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import static com.vk_media.vkmedia.security.FirebaseOauthHttpConfigurer.firebaseOauthHttpConfigurer;

public class WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain httpFilterChain(HttpSecurity http) throws Exception {
        http.apply(firebaseOauthHttpConfigurer());
        http.antMatcher("/").anonymous();
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurity() throws Exception {
        return web -> web.ignoring().anyRequest();
    }

}
