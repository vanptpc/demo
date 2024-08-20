package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

	@GetMapping("/index/admin")
	public String indexAdmin() {
		return "admin/index";
	}

	@GetMapping("/components")
	public String component() {
		return "admin/components";
	}

	
}
