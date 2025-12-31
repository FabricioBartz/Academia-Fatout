package com.academia.config;

import com.academia.model.Instrutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Habilitar CSRF com CookieCsrfTokenRepository para disponibilidade em páginas
        http.csrf(csrf -> csrf
            .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())
        );

        // Autorizações: manter o comportamento atual (quase tudo livre)
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/"),
                                 new AntPathRequestMatcher("/index"),
                                 new AntPathRequestMatcher("/css/**"),
                                 new AntPathRequestMatcher("/js/**"),
                                 new AntPathRequestMatcher("/uploads/**"),
                                 new AntPathRequestMatcher("/images/**"),
                                 new AntPathRequestMatcher("/webjars/**"),
                                 new AntPathRequestMatcher("/instrutor/login/**"),
                                 new AntPathRequestMatcher("/aluno/**"))
                .permitAll()
                // Páginas de gestão de instrutores exigem ADMIN
                .requestMatchers(new AntPathRequestMatcher("/instrutor/instrutores"),
                                 new AntPathRequestMatcher("/instrutor/instrutores/**"))
                .hasRole("ADMIN")
                .anyRequest().permitAll()
        );

        // Filtro que sincroniza a sessão (HttpSession) com o SecurityContext
        http.addFilterBefore(new SessionAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // Em caso de acesso negado/sem autenticação, redireciona para a página de login do instrutor
        http.exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
            response.sendRedirect("/instrutor/login");
        }));

        return http.build();
    }

    // Encoder disponível para futura migração de senhas (não usado ainda)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Filtro simples que lê o atributo "instrutor" da HttpSession e popula o SecurityContext
     * com as authorities INSTRUTOR e ADMIN (se aplicável). Mantém compatibilidade com o login atual.
     */
    static class SessionAuthenticationFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            // Se já existe autenticação, segue
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    Object obj = session.getAttribute("instrutor");
                    if (obj instanceof Instrutor instrutor) {
                        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                        authorities.add(new SimpleGrantedAuthority("ROLE_INSTRUTOR"));
                        if (instrutor.isAdmin()) {
                            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                        }
                        // Usa email como principal se existir, senão CPF
                        String principal = instrutor.getEmail() != null ? instrutor.getEmail() : instrutor.getCpf();
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}
