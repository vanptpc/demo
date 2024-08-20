package com.example.demo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Cart;
import com.example.demo.model.Firm;
import com.example.demo.model.MovieVideo;
import com.example.demo.model.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.FirmRepository;
import com.example.demo.repository.MovieVideoRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CartService;
import com.example.demo.service.CheckOutService;
import com.example.demo.service.CoinsService;
import com.example.demo.service.FirmService;
import com.example.demo.service.MovieFirmService;
import com.example.demo.service.MovieVideoService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FirmController {

	@Value("${upload.path}")
	private String pathUploadImage;

	@Autowired
	private FirmRepository firmRepository;
	@Autowired
	FirmService firmService;
	@Autowired
	CartService cartService;
	@Autowired
	CheckOutService checkOutService;
	@Autowired
	CoinsService coinsService;
	@Autowired
	MovieFirmService service;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private MovieVideoRepository movieVideoRepository;
	@Autowired
	private UserRepository repository;
	@Autowired
	MovieVideoService movieVideoService;
	@Autowired
	MovieVideoService videoService;
	@Autowired
	FirmRepository firmRepository2;

	@GetMapping("/forms")
	public String showForm(Model model) {
		model.addAttribute("firm", new Firm());
		return "admin/forms";
	}

	@PostMapping("/addFirm")
	public String addFirm(@ModelAttribute Firm firm, BindingResult result,
			@RequestParam("img_firm") MultipartFile imgFile, @RequestParam("link_video") MultipartFile videoFile,
			Model model) {

		if (!imgFile.isEmpty()) {
			try {
				String imgFilename = System.currentTimeMillis() + "_" + imgFile.getOriginalFilename();
				Path imgPath = Paths.get(pathUploadImage + File.separator + imgFilename);
				Files.write(imgPath, imgFile.getBytes());
				firm.setImg_firm(imgFilename);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!videoFile.isEmpty()) {
			try {
				String videoFilename = System.currentTimeMillis() + "_" + videoFile.getOriginalFilename();
				Path videoPath = Paths.get(pathUploadImage + File.separator + videoFilename);
				Files.write(videoPath, videoFile.getBytes());
				firm.setLink_video(videoFilename);
				firm.setStatus(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		firmRepository.save(firm);

		// Lấy danh sách người dùng và firm từ cơ sở dữ liệu
		List<User> users = repository.findAll();
		List<Firm> firms = firmRepository2.findAll();

		// Vòng lặp kiểm tra và thêm mới các MovieVideo
		for (User user : users) {
			for (Firm f : firms) {
				boolean exists = movieVideoRepository.existsByUserIdAndFirmId(user.getId(), f.getId());

				// Nếu bản ghi chưa tồn tại, thêm mới
				if (!exists) {
					MovieVideo movieVideo = new MovieVideo();
					movieVideo.setUser(user);
					movieVideo.setFirm(f);
					movieVideo.setStatus(0); 
					movieVideoRepository.save(movieVideo);
				}
			}
		}

		model.addAttribute("successMessage", "Firm successfully added!");
		model.addAttribute("firm", new Firm());
		return "admin/forms";
	}

	@GetMapping("/firm/{id}")
	public String getFirmById(@PathVariable("id") Long id, Model model, HttpSession session) {
		Long idUser = (Long) session.getAttribute("id_user");
		Optional<Firm> firmOptional = firmRepository.findById(id);

		if (firmOptional.isPresent()) {
			Firm firm = firmOptional.get();
			firmService.updateStatusFirm(firm.getId());
			long id_Cart = cartService.saveCart(firm.getId(), idUser);
			checkOutService.addCheckout(idUser, id_Cart);
			boolean coin = coinsService.updateCoins(idUser, firm.getCoins_video(), firm.getId(), model, session);
			if (coin == true) {
				service.saveFirm(firm.getId(), idUser);

				movieVideoService.updateMovie(idUser, firm.getId());
			}

			model.addAttribute("firm", firm);
			return "redirect:/checkout-success?id=" + idUser;

		} else {

			return "redirect:/";
		}
	}

	@GetMapping("/checkout-success")
	public String CheckOutSuccess(@RequestParam("id") long idUser, HttpSession session, Model model) {

		Boolean islogin = (Boolean) session.getAttribute("islogin");

		if (islogin == null) {
			islogin = false;
		}

		session.setAttribute("islogin", true);
		List<Firm> firms = firmRepository.findAll();
		List<Cart> carts = cartRepository.findCartsWithUser(idUser);

		Optional<User> optional = repository.findById(idUser);
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
		return "web/test";
	}

	@PostMapping("/clear-session-messages")
	public void clearSessionMessages(HttpSession session) {
		session.removeAttribute("successMessage");
		session.removeAttribute("errorMessage");
	}

}
