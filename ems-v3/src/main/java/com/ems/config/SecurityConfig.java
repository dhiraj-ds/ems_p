package com.ems.config;
import com.ems.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration:
 *  - Stateless session (JWT in HttpOnly cookie)
 *  - DAO Authentication Provider
 *  - Custom login/register/landing pages
 *  - JWT filter before standard auth filter
 *  - Redirects to /login if unauthenticated
 */
@Configuration @EnableWebSecurity @RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl uds;
    private final JwtAuthFilter jwtFilter;

    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider() {
        var p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(daoAuthProvider())
            .csrf(csrf -> csrf.disable())                         // JWT cookie approach; enable in production
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/about", "/features",                   // public landing pages
                    "/auth/login", "/auth/logout", "/auth/refresh", // auth endpoints
                    "/auth/register", "/register",                // registration (employees only)
                    "/login", "/css/**", "/js/**", "/images/**"   // static resources + login page
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/employee/**").hasRole("EMPLOYEE")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                // Redirect to custom login page when unauthenticated
                .authenticationEntryPoint((req, res, e) -> res.sendRedirect("/login?expired"))
                // Redirect to error page when unauthorized (wrong role)
                .accessDeniedHandler((req, res, e) -> res.sendRedirect("/login?denied"))
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}