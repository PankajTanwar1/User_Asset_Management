package com.assets.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	
	@GetMapping("/login")
	public String login()
	{
		return "login";
	}
	
	@GetMapping("/faq")
	public String faq() {	
		return"faq";
	}
	
	@GetMapping("/MonkeyDLuffy")
	public String monkeyDLuffy() {	
		return"MonkeyDLuffy";
	}

}
