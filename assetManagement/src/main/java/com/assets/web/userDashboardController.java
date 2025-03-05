package com.assets.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.assets.model.User;
import com.assets.repository.UserRepository;



@Controller
@RequestMapping("/userDashboard")
public class userDashboardController {
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public String displayUserDashboard(Model model) {
        try {
            String user = returnUsername();
            model.addAttribute("userDetails", user);
        } catch (Exception e) {
            
            model.addAttribute("userDetails", "User not found");
        }
        return "userDashboard";
    }

    private String returnUsername() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        UserDetails user = (UserDetails) securityContext.getAuthentication().getPrincipal();
        User users = userRepository.findByEmail(user.getUsername());
        if (users != null) {
            return users.getFirstName();
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
