package com.vk_media.vkmedia.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class FirebaseOauthHttpConfigurer extends AbstractHttpConfigurer<FirebaseOauthHttpConfigurer, HttpSecurity> {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/photos/get*Rest")
                .authorizeRequests()
                .anyRequest()
                .authenticated().and().antMatcher("/").anonymous();

        http.oauth2ResourceServer()
                .jwt();
    }

    public static FirebaseOauthHttpConfigurer firebaseOauthHttpConfigurer() {
        return new FirebaseOauthHttpConfigurer();
    }
}

