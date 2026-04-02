package br.com.ordempro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain configurarSeguranca(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // simplifica para desenvolvimento

                .authorizeHttpRequests(authorize -> authorize
                        // libera acesso sem login
                        .requestMatchers("/login", "/css/**", "/js/**", "/img/**").permitAll()

                        // qualquer outra rota precisa estar logado
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login") // sua tela customizada
                        .defaultSuccessUrl("/ordens", true)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}