package com.datingapp.racontre.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/register", "/login","/terms","privacy", "/css/**", "/uploads/**").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .defaultSuccessUrl("/profile", true)
                                .failureUrl("/login?error=true") // Redirect on error
                                .permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout") // The URL to trigger logout
                                .logoutSuccessUrl("/login?logout=true") // Redirect to login after logout
                                .invalidateHttpSession(true) // Invalidate session
                                .deleteCookies("JSESSIONID") // Delete cookies
                                .permitAll()
                );

        return http.build();
    }

}
