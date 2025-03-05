package com.assets.config;

import java.io.IOException;
import java.util.Collection;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
    	String username = authentication.getName();
        System.out.println("Authentication successful for user: " + username);

        String redirectUrl = determineRedirectUrl(authentication);

        if (redirectUrl == null) {
            throw new IllegalStateException("No redirect URL found for the user roles");
        }

        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }

    private String determineRedirectUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
                return "/userDashboard";
            } else if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                return "/adminDashboard";
            }
        }
        return null;
    }
}





//package com.assets.config;
//
//import java.io.IOException;
//import java.util.Set;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Configuration
//public class CustomSuccessHandler implements AuthenticationSuccessHandler{
//  
//  @Override
//  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException{
//    Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
//    
//    if(roles.contains("ROLE_ADMIN")) {
//      
//      response.sendRedirect("/adminDashboard");
//    }
//    else if(roles.contains("ROLE_USER")) {
//      response.sendRedirect("/userDashboard");
//    }
//   
//  }
//}