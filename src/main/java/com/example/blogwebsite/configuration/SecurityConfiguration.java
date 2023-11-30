package com.example.blogwebsite.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfiguration {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        userDetailsManager.setUsersByUsernameQuery("select email, password, true as enabled from member where email=?");
        userDetailsManager.setAuthoritiesByUsernameQuery("select email, authority from member where email=?");
        return userDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection
                .authorizeHttpRequests(configurer ->
                        configurer
                                .requestMatchers("/api/getallpost", "/api/getpostbyid/**",
                                        "/", "/viewpost", "/register", "/saveRegisteredUser",
                                        "/api/saveRegisteredUser/**", "/api/savecomment/**",
                                        "/savecomment", "/access-denied", "/authenticateTheUser",
                                        "/showMyLoginPage").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(form ->
                        form
                                .loginPage("/showMyLoginPage")
                                .loginProcessingUrl("/authenticateTheUser")
                                .defaultSuccessUrl("/")
                                .permitAll()
                )
                .logout(logout -> logout.permitAll())
                .exceptionHandling(configurer ->
                        configurer.accessDeniedPage("/access-denied")
                )
        ;
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

}
