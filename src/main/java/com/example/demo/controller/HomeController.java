package com.example.demo.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.dto.FirmDto;
import com.example.demo.model.Cart;
import com.example.demo.model.Firm;
import com.example.demo.model.MovieVideo;
import com.example.demo.model.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.FirmRepository;
import com.example.demo.repository.MovieVideoRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CategoryService;
import com.example.demo.service.EpisodeService;
import com.example.demo.service.FirmService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	@Autowired
	private FirmRepository firmRepository;
	@Autowired
	FirmService firmService;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private MovieVideoRepository movieVideoRepository;
	@Autowired
	private UserRepository repository;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private EpisodeService episodeService;

	@GetMapping("/")
	public String home(Model model, HttpSession session) {
		List<FirmDto> firms = episodeService.getFirm();
		model.addAttribute("firms", firms);
		List<Firm> topFirms = firmService.getTop5MostViewedFirms();
		model.addAttribute("categoryList", categoryService.getAllCategories());
		model.addAttribute("topFirms", topFirms);
		session.setAttribute("islogin", false);
		return "web/test";
	}

	@GetMapping("/home")
	public String quit(Model model, HttpSession session) {
		Long idUser = (Long) session.getAttribute("id_user");
		Boolean islogin = (Boolean) session.getAttribute("islogin");
		session.setAttribute("status", true);
		if (islogin == null) {
			islogin = false;
		}
		model.addAttribute("categoryList", categoryService.getAllCategories());
		List<Firm> topFirms = firmService.getTop5MostViewedFirms();
		model.addAttribute("topFirms", topFirms);
		List<FirmDto> firmDtos = episodeService.getFirm();
		
		List<Firm> firms = firmRepository.findAll();
		List<Cart> carts = cartRepository.findCartsWithUser(idUser);

		Optional<User> optional = repository.findById(idUser);
		session.setAttribute("islogin", true);
		if (optional.isPresent()) {
			Map<Firm, List<MovieVideo>> firmMovieVideos = new HashMap<>();

			for (Firm firm : firms) {
				// Tạo một đối tượng MovieVideo mới cho mỗi Firm

				List<MovieVideo> list = movieVideoRepository.findByUserIdAndFirmId(idUser, firm.getId());

				firmMovieVideos.put(firm, list);
			}

			model.addAttribute("firmMovieVideos", firmMovieVideos);
		}

		model.addAttribute("firms", firmDtos);
		return "web/test";
	}

}
