package com.assets.web;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.assets.model.Role;
import com.assets.model.User;
import com.assets.service.UserNotFoundException;
import com.assets.service.UserService;
import com.assets.web.dto.UserRegistrationDto;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController {

    private final UserService userService;

    public UserRegistrationController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
         return "registration";
    }
    
    // Admin-specific registration page
    @GetMapping("/userRegistrationByAdmin")
    public String showAdminRegistrationForm(Model model) {
        // Only admins can access this page
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "userRegistrationByAdmin";
        } else {
            return "redirect:/access-denied"; // Redirect if not an admin
        }
    }

    @PostMapping
    public String registerUserAccount(UserRegistrationDto registrationDto, RedirectAttributes redirectAttributes) {
        try {
            userService.save(registrationDto);
            redirectAttributes.addFlashAttribute("message", "Registration Successful!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration Failed: " + e.getMessage()); 
        }
        return "redirect:/registration";
    }
    
    @PostMapping("/userRegistrationByAdmin")
    public String registerUserAccountByAdmin(UserRegistrationDto registrationDto, RedirectAttributes redirectAttributes) {
        try {
            userService.save(registrationDto);
            redirectAttributes.addFlashAttribute("message", "Registration Successful!");
            
            // Check if the user is an admin and redirect accordingly
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                // If the current user is an admin, redirect to the admin-specific page after registration
                return "redirect:/registration/userRegistrationByAdmin";
            } else {
                // If the user is not an admin, redirect to the normal registration page
                return "redirect:/registration";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration Failed: " + e.getMessage());
            return "redirect:/registration";
        }
    }

    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") long id, Model model) {
        try {
            User user = userService.getUserById(id);
//          model.addAttribute("user", user);
            
            UserRegistrationDto registrationDto = new UserRegistrationDto(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getGender(),
                    user.getEmail(),
                    "", // leave password empty
                    user.getRole().getName(),
                    user.getAddress(),
                    user.getCity(),
                    user.getState(),
                    user.getZip()
            );
            
            model.addAttribute("user",registrationDto); 
            return "updateUser";
           
        } catch (UserNotFoundException e) {
            model.addAttribute("errorMessage", "User not found!");
            return "redirect:/userList";
        }
        
    }
    
//    @GetMapping("/showFormForUpdate/{id}")
//    public String showFormForUpdate(@PathVariable(value = "id") long id, Model model) {
//        try {
//            User user = userService.getUserById(id);
//
//            // Step 1: Convert the user's roles (Collection<Role>) to a single String role for the form
//            String userRole = user.getRoles().stream()
//                .findFirst() // Assuming the user has one role, retrieve it
//                .map(Role::getName) // Get the role name (e.g., "ROLE_USER")
//                .orElse("USER"); // Default to "USER" if no role is found
//
//            // Convert role name to simpler format (e.g., "ROLE_USER" -> "USER")
//            if (userRole.equals("ROLE_USER")) {
//                userRole = "USER";
//            } else if (userRole.equals("ROLE_ADMIN")) {
//                userRole = "ADMIN";
//            }
//
//            // Step 2: Create a DTO to pass to the form
//            UserRegistrationDto userDto = new UserRegistrationDto();
//            userDto.setId(user.getId()); 
//            userDto.setFirstName(user.getFirstName());
//            userDto.setLastName(user.getLastName());
//            userDto.setEmail(user.getEmail());
//            // We're passing the user role as a string to the DTO
//            userDto.setRole(userRole);
//
//            // Step 3: Add the DTO to the model
//            model.addAttribute("user", userDto);
//
//        } catch (UserNotFoundException e) {
//            model.addAttribute("errorMessage", "User not found!");
//        }
//
//        return "updateUser";
//    }


    @PostMapping("/updateUser/{id}")
    public String updateUser(@PathVariable(value = "id") long id, UserRegistrationDto registrationDto, RedirectAttributes redirectAttributes) {
        try {
            userService.update(registrationDto, id);
            redirectAttributes.addFlashAttribute("message", "User Updated Successfully!");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
        }
        
        return "redirect:/UserList"; 

    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable(value = "id") long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("message", "User Deleted Successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Deletion Failed: " + e.getMessage());
        }
        return "redirect:/UserList";
    }
}