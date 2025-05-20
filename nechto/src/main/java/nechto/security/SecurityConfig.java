package nechto.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().and()
                .authorizeHttpRequests((authorize) -> authorize
                        .antMatchers(GET).permitAll()
                        .antMatchers("/scores/**", "/game/**").hasAnyRole("ADMIN", "OWNER")
                        .antMatchers(PUT,"/user/**").hasAnyRole("ADMIN", "OWNER")
                        .antMatchers(POST,"/user/**").hasAnyRole("ADMIN", "OWNER")
                        .antMatchers("/**").hasRole("OWNER")
                        .anyRequest()
                        .authenticated())
                        .httpBasic(Customizer.withDefaults());
        return http.build();
    }

}

