package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Cart;
import com.example.demo.model.Firm;
import com.example.demo.model.MovieVideo;
import com.example.demo.model.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.FirmRepository;
import com.example.demo.repository.MovieVideoRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CoinsService;
import com.example.demo.service.FirmService;
import com.example.demo.service.UserRoleService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class LoginController {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	UserService userService;
	@Autowired
	CoinsService service;
	@Autowired
	private FirmRepository firmRepository;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private MovieVideoRepository movieVideoRepository;
	@Autowired
	private UserRepository repository;
	@Autowired
	UserRoleService roleService;
	@Autowired
	FirmService firmService;

	@GetMapping("/login")
	public String home() {
		return "web/login";
	}

	@PostMapping("/login")
	public String login(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
		Optional<User> user = userService.checkLogin(email, password);
		if (user.isPresent()) {
			double coin = service.getCoinsUser(user.get());
			session.setAttribute("id_user", user.get().getId());
			session.setAttribute("coins", coin);
			session.setAttribute("status", true);
			System.out.println("kq" + coin);

			return "redirect:/login-success?id=" + user.get().getId();
		} else {
			model.addAttribute("error", "Thông tin đăng nhập không đúng");
			return "web/login";
		}
	}

	@GetMapping("/login-success")
	public String loginSuccess(@RequestParam("id") long idUser, HttpSession session, Model model) {

		System.out.println("User ID: " + idUser);

		Boolean islogin = (Boolean) session.getAttribute("islogin");
		List<Firm> topFirms = firmService.getTop5MostViewedFirms();
		model.addAttribute("topFirms", topFirms);
		if (islogin == null) {
			islogin = false;
		}

		session.setAttribute("islogin", true);
		List<Firm> firms = firmRepository.findAll();

		Optional<User> optional = repository.findById(idUser);
		String username = optional.get().getName();
		session.setAttribute("username", username);
		session.setAttribute("islogin", true);
		if (optional.isPresent()) {
			Map<Firm, List<MovieVideo>> firmMovieVideos = new HashMap<>();

			for (Firm firm : firms) {
				// Tạo một đối tượng MovieVideo mới cho mỗi Firm

				List<MovieVideo> list = movieVideoRepository.findByUserIdAndFirmId(idUser, firm.getId());
				System.out.println("Firm: " + firm.getTittle_firm() + ", Number of Videos: " + list.size());

				firmMovieVideos.put(firm, list);
			}

			model.addAttribute("firmMovieVideos", firmMovieVideos);
		}

		model.addAttribute("firms", firms);
		long id = roleService.role(idUser);
		if (id == 1) {
			return "web/test";
		} else {
			return "admin/index";
		}

	}
}
