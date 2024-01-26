package com.shop.config;

import com.shop.service.CustomOAuth2SuccessHandler;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception{
        // CSRF
        http.csrf(c -> {});
        // REQUEST
        http.authorizeHttpRequests(configure -> {
            configure.requestMatchers("/user/cart").authenticated();
            configure.anyRequest().permitAll();
        });
        // FORM-LOGIN
        http.formLogin(configure -> {
            configure.loginPage("/user/login")
                    .loginProcessingUrl("/user/login")
                    .defaultSuccessUrl("/");
            configure.permitAll();
        });
        // OAUTH2
        http.oauth2Login(configure -> {
            configure.loginPage("/user/login").permitAll();
            configure.successHandler(new CustomOAuth2SuccessHandler());
        });
        // REMENBER ME

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
