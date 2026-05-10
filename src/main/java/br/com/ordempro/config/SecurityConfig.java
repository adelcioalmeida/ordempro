package br.com.ordempro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final DaoAuthenticationProvider authenticationProvider;

    public SecurityConfig(DaoAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain configurarSeguranca(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider)

                .authorizeHttpRequests(authorize -> authorize

                        // Rotas públicas
                        .requestMatchers(
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/favicon.ico"
                        ).permitAll()

                        // ADMIN, GERENTE e VENDEDOR podem alterar a própria senha
                        .requestMatchers("/usuarios/alterar-senha")
                        .hasAnyRole("ADMIN", "GERENTE", "VENDEDOR")

                        // Apenas ADMIN pode acessar o gerenciamento de usuários
                        .requestMatchers("/usuarios/**")
                        .hasRole("ADMIN")

                        // ADMIN e GERENTE podem acessar cidades e serviços
                        .requestMatchers(
                                "/cidades/**",
                                "/servicos/**"
                        ).hasAnyRole("ADMIN", "GERENTE")

                        // VENDEDOR NÃO pode editar ou excluir cliente
                        .requestMatchers(
                                "/clientes/editar/**",
                                "/clientes/excluir/**"
                        ).hasAnyRole("ADMIN", "GERENTE")

                        // ADMIN, GERENTE e VENDEDOR podem cadastrar clientes
                        .requestMatchers(
                                "/clientes/novo",
                                "/clientes/salvar"
                        ).hasAnyRole("ADMIN", "GERENTE", "VENDEDOR")

                        // ADMIN, GERENTE e VENDEDOR podem visualizar e buscar clientes
                        .requestMatchers(
                                "/clientes",
                                "/clientes/**"
                        ).hasAnyRole("ADMIN", "GERENTE", "VENDEDOR")

                        // ADMIN, GERENTE e VENDEDOR podem abrir a tela de edição.
                        // A regra fina está no OrdemServicoController:
                        // vendedor só edita ordem ABERTA e não altera status.
                        .requestMatchers("/ordens/editar/**")
                        .hasAnyRole("ADMIN", "GERENTE", "VENDEDOR")

                        // Apenas ADMIN e GERENTE podem cancelar ordens
                        .requestMatchers("/ordens/cancelar/**")
                        .hasAnyRole("ADMIN", "GERENTE")

                        // Bloqueia exclusão real de ordens
                        .requestMatchers("/ordens/excluir/**")
                        .denyAll()

                        // ADMIN, GERENTE e VENDEDOR podem visualizar e abrir ordens
                        .requestMatchers(
                                "/ordens",
                                "/ordens/**"
                        ).hasAnyRole("ADMIN", "GERENTE", "VENDEDOR")

                        // Qualquer outra rota exige login
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/ordens", true)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/acesso-negado")
                );

        return http.build();
    }
}