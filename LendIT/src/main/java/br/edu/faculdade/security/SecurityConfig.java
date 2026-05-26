package br.edu.faculdade.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. Adicionamos o /h2-console/** na lista de permitidos
                        .requestMatchers("/login", "/cadastro", "/css/**", "/js/**", "/h2-console/**").permitAll() 
                        .anyRequest().authenticated() 
                )
                .formLogin(form -> form
                        .loginPage("/login") 
                        .defaultSuccessUrl("/dashboard", true) 
                        .permitAll()
                )
                .logout(logout -> logout.permitAll())
                
                // 2. Desativamos a proteção CSRF apenas para a URL do banco de dados
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                
                // 3. Permitimos que o navegador renderize os "Frames" do H2 Console
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Criptografia forte para a senha
    }
}