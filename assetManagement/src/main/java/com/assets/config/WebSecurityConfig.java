package com.assets.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	@Autowired
	AuthenticationSuccessHandler successHandler;
	
	@Bean
	PasswordEncoder passwordEncoder() {
	      return new BCryptPasswordEncoder();
	  }
	
	@Bean
    DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
       DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       authProvider.setUserDetailsService(userDetailsService);
       authProvider.setPasswordEncoder(passwordEncoder);
       return authProvider;
   }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	http  
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorizeHttpRequests ->
            	authorizeHttpRequests 
            		.requestMatchers("/js/**", "/css/**", "/image/**").permitAll()
            		.requestMatchers("/registration").permitAll()
            		.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
            		.requestMatchers("/user/**").hasAuthority("ROLE_USER")
            		.requestMatchers("/assetList").hasRole("ADMIN")
            		.anyRequest().authenticated()
            )
            .formLogin(formLogin ->
                formLogin
                    .loginPage("/login")
                    .successHandler(successHandler)
                    .permitAll()
            )
            .logout(logout ->
                logout
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
            );

        return http.build();
    }
    
}