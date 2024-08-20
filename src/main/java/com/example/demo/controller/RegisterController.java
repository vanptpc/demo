package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Firm;
import com.example.demo.model.MovieVideo;
import com.example.demo.model.User;
import com.example.demo.repository.FirmRepository;
import com.example.demo.repository.MovieFirmRepository;
import com.example.demo.repository.MovieVideoRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CoinsService;
import com.example.demo.service.FirmService;
import com.example.demo.service.UserRoleService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class RegisterController {
	@Autowired
	private UserService userService;
	@Autowired
	private UserRoleService userRoleService;
	@Autowired
	private CoinsService service;
	@Autowired
	private FirmRepository firmRepository;
	@Autowired
	private MovieVideoRepository movieVideoRepository;
	@Autowired
	private UserRepository repository;
	@Autowired
	UserRoleService roleService;
	@Autowired
	FirmService firmService;

	@GetMapping("/register")
	public String home() {
		return "web/signup";
	}

	@PostMapping("/signup")
	public String login(@RequestParam String email, @RequestParam String username, @RequestParam String password,
			HttpSession session, Model model) {
		if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
			model.addAttribute("error", "Vui lòng nhập đầy đủ thông tin");
			return "web/signup";
		} else {
			boolean result = userService.checkEmail(email);
			if (result == false) {
				long idUser = userService.saveUser(email, username, password);

				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();

				}

				double coin = service.saveCoin(50, idUser);
				session.setAttribute("id_user", idUser);
				roleService.addUserRole(1, idUser);
				session.setAttribute("coins", coin);
				session.setAttribute("status", true);
				model.addAttribute("success", "Đăng kí thành công");
				return "redirect:/register-success?id=" + idUser;
			} else {
				model.addAttribute("error", "Email này đã có người dùng");
				return "web/signup";
			}
		}

	}

	@GetMapping("/register-success")
	public String loginSuccess(@RequestParam("id") long idUser, HttpSession session, Model model) {

		List<Firm> firms = firmRepository.findAll();
		Optional<User> optional = repository.findById(idUser);
		session.setAttribute("islogin", true);
		List<Firm> topFirms = firmService.getTop5MostViewedFirms();
		model.addAttribute("topFirms", topFirms);
		if (optional.isPresent()) {
			User user = optional.get();
			Map<Firm, List<MovieVideo>> firmMovieVideos = new HashMap<>();

			for (Firm firm : firms) {
				// Tạo một đối tượng MovieVideo mới cho mỗi Firm
				MovieVideo movieVideo = new MovieVideo();
				movieVideo.setFirm(firm);
				movieVideo.setUser(user);
				movieVideo.setStatus(0);

				// Lưu từng MovieVideo vào cơ sở dữ liệu
				movieVideoRepository.save(movieVideo);

				// Lấy danh sách MovieVideo tương ứng với User và Firm hiện tại
				List<MovieVideo> list = movieVideoRepository.findByUserIdAndFirmId(idUser, firm.getId());
				System.out.println("Firm: " + firm.getId() + ", List Size: " + list.size());
				// Đưa danh sách vào map với key là Firm
				firmMovieVideos.put(firm, list);
			}

			// Thêm map vào model
			model.addAttribute("firmMovieVideos", firmMovieVideos);
		}

		model.addAttribute("firms", firms);
		return "web/test";
	}

}
