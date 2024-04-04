package org.example.userhandleapi.config.JWT;

import org.example.userhandleapi.Service.UserInfoService;
import org.example.userhandleapi.config.JWT.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;
    @Bean
    public UserDetailsService userDetailsService(){
        return new UserInfoService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        //disable CORS
        httpSecurity.cors(corsConfig->corsConfig.configurationSource(getConfigurationSource()));

        //disable csrf
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        //filter our request
        httpSecurity.authorizeHttpRequests(
                requestMatcher->
                        requestMatcher.requestMatchers("/auth/welcome","/auth/addUser","/auth/login","/patient/registerPatient","/admin/addDoctor/","/hospital/add-hospital","/email/sendEmail","/email/valOtp").permitAll()
//                                .requestMatchers("/api/signUp").permitAll()
//                                .requestMatchers("/api/hospital/register").permitAll()
//                                .requestMatchers("/api/hospital/register").permitAll()
                                //.requestMatchers("/auth/getUsersr").hasRole("USER_ROLES")
                                .anyRequest().authenticated()


        );




        //session policy [stateless]
        httpSecurity.sessionManagement(
                sessionConfig->sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // jwt authentication filter
        httpSecurity.addFilterBefore(jwtFilter,
                UsernamePasswordAuthenticationFilter.class);

        //httpSecurity.cors(Customizer.withDefaults());


        return httpSecurity.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    private static CorsConfigurationSource getConfigurationSource(){
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedMethod("OPTIONS");
        corsConfiguration.setAllowedMethods(List.of("*"));
        corsConfiguration.addAllowedHeader("*");

        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080"));
        corsConfiguration.setAllowedHeaders(List.of("Content-Type","text/plain","Authorization"));

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return  source;
    }

}
