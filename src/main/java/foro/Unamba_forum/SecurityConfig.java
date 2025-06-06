package foro.Unamba_forum;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.disable()).csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth2/callback/**","/career/**","/user/**","/userprofile/**","/totals/**","/publication/**","/reaction/**","/commentpublication/**","/follow/**","/rol/**","/reactioncomment/**","/responsecomment/**","/category/**","/notes/**","/notification/**").permitAll()
                        .anyRequest().authenticated());
        return http.build();
    }
}