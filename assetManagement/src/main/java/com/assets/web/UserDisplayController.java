package com.assets.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.assets.model.User;
import com.assets.service.UserService;

@Controller
public class UserDisplayController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/UserList")
    public String viewUserList(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        return findPaginated(1, "firstName", "asc", model, keyword);
    }

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,
                                @RequestParam(value = "sortField", defaultValue = "firstName") String sortField,
                                @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
                                Model model,
                                @RequestParam(value = "keyword", required = false) String keyword) {
    int pageSize = 10;
    Page<User> page;

    // Validate sortDir
    if (!sortDir.equals("asc") && !sortDir.equals("desc")) {
        sortDir = "asc";
    }

    if (keyword != null && !keyword.isEmpty()) {
        page = userService.searchUsers(keyword, pageNo, pageSize, sortField, sortDir);
    } else {
        page = userService.findPaginated(pageNo, pageSize, sortField, sortDir);
    }

    List<User> listUser = page.getContent(); 
        
    model.addAttribute("currentPage", pageNo);
    model.addAttribute("totalPages", page.getTotalPages());
    model.addAttribute("totalItems", page.getTotalElements());
        
    model.addAttribute("sortField", sortField);
    model.addAttribute("sortDir", sortDir);
    model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
    model.addAttribute("listUser", listUser);
    model.addAttribute("keyword", keyword);

    return "UserList";
    }
}

