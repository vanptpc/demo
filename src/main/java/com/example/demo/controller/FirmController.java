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
import com.example.demo.model.Category;
import com.example.demo.model.Firm;
import com.example.demo.model.MovieVideo;
import com.example.demo.model.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.FirmRepository;
import com.example.demo.repository.MovieVideoRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CartService;
import com.example.demo.service.CategoryService;
import com.example.demo.service.CheckOutService;
import com.example.demo.service.CoinsService;
import com.example.demo.service.FirmService;
import com.example.demo.service.MovieFirmService;
import com.example.demo.service.MovieVideoService;

import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
	@Autowired
	private CategoryService categoryService;
	@Autowired
	MovieFirmService movieFirmService;

	@GetMapping("/forms")
	public String showForm(Model model) {
		model.addAttribute("firm", new Firm());
		model.addAttribute("categories", categoryService.getAllCategories());
		return "admin/forms";
	}

	@GetMapping("/history-firm")
	public String history(Model model, HttpSession session) {
		Long idUser = (Long) session.getAttribute("id_user");

		// Giả sử bạn có service để lấy danh sách firm theo id người dùng
		List<Firm> firms = movieFirmService.getAllFirmsByUserId(idUser);
		model.addAttribute("categoryList", categoryService.getAllCategories());

		model.addAttribute("firms", firms);
		return "web/history";
	}

	@GetMapping("/firms-by-category/{id}")
	public String getFirmsByCategory(@PathVariable("id") Long categoryId, Model model, HttpSession session) {
		session.setAttribute("id_category", categoryId);
		Long idUser = (Long) session.getAttribute("id_user");
		Boolean islogin = (Boolean) session.getAttribute("islogin");

		if (idUser == null) {
			session.setAttribute("islogin", false);
		} else {
			session.setAttribute("status", true);
		}

		Optional<Category> categoryOptional = categoryService.getCategoryById(categoryId);
		model.addAttribute("categoryList", categoryService.getAllCategories());

		if (categoryOptional.isPresent()) {
			Category category = categoryOptional.get();
			List<Firm> firms = firmService.getFirmsByCategory(category);
			model.addAttribute("firms", firms);
			model.addAttribute("cat", category);
			List<Firm> topFirms = firmService.getTop5MostViewedFirms();
			model.addAttribute("topFirms", topFirms);

			Map<Firm, List<MovieVideo>> firmMovieVideos = new HashMap<>();

			for (Firm firm : firms) {
				// Tạo một đối tượng MovieVideo mới cho mỗi Firm

				List<MovieVideo> list = movieVideoRepository.findByUserIdAndFirmId(idUser, firm.getId());
				System.out.println("Firm: " + firm.getTittle_firm() + ", Number of Videos: " + list.size());

				firmMovieVideos.put(firm, list);
			}

			model.addAttribute("firmMovieVideos", firmMovieVideos);

		}

		return "web/category";
	}

	@GetMapping("/firm-by-category/{id}")
	public String getFirmByCategory(@PathVariable("id") Long id, Model model, HttpSession session) {
		Long categoryId = (Long) session.getAttribute("id_category");
		Long idUser = (Long) session.getAttribute("id_user");
		Optional<Firm> firmOptional = firmRepository.findById(id);
		model.addAttribute("categoryList", categoryService.getAllCategories());
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
			return "redirect:/firms-by-category/" + categoryId;
		} else {

			return "redirect:/";
		}
	}

	@PostMapping("/addFirm")
	public String addFirm(@ModelAttribute Firm firm, BindingResult result,
			@RequestParam("img_firm") MultipartFile imgFile, @RequestParam("link_video") MultipartFile videoFile,
			@RequestParam("link_video_traller") MultipartFile videoTraller, Model model, HttpSession session) {
		if (firm.getPractice() != null && firm.getTotal_episodes() != null) {
			if (firm.getPractice() > firm.getTotal_episodes()) {
				result.rejectValue("practice", "error.firm", "Tập phim không được lớn hơn tổng tập phim");
			}
		}
		if (!imgFile.isEmpty()) {
			try {
				String imgFilename = System.currentTimeMillis() + "_"
						+ URLEncoder.encode(imgFile.getOriginalFilename(), StandardCharsets.UTF_8.toString());
				Path imgPath = Paths.get(pathUploadImage + File.separator + imgFilename);
				Files.write(imgPath, imgFile.getBytes());
				firm.setImg_firm(imgFilename);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!videoFile.isEmpty()) {
			try {
				String videoFilename = System.currentTimeMillis() + "_"
						+ URLEncoder.encode(videoFile.getOriginalFilename(), StandardCharsets.UTF_8.toString());
				Path videoPath = Paths.get(pathUploadImage + File.separator + videoFilename);
				Files.write(videoPath, videoFile.getBytes());
				firm.setLink_video(videoFilename);
				firm.setStatus(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!videoTraller.isEmpty()) {
			try {
				String videoTrallerFilename = System.currentTimeMillis() + "_"
						+ URLEncoder.encode(videoTraller.getOriginalFilename(), StandardCharsets.UTF_8.toString());
				Path videoTrallerPath = Paths.get(pathUploadImage + File.separator + videoTrallerFilename);
				Files.write(videoTrallerPath, videoTraller.getBytes());
				firm.setLink_video_traller(videoTrallerFilename);
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

		session.setAttribute("successMessage", "Thêm thành công!");
		model.addAttribute("firm", new Firm());
		return "redirect:/forms";
	}

	@PostMapping("/check-firm-name")
	@ResponseBody
	public String checkFirmName(@RequestParam("name_firm") String nameFirm) {
		// Tìm các firm với tên name_firm
		List<Firm> existingFirms = firmRepository.findByName_firm(nameFirm.toLowerCase());

		// Nếu tồn tại firm có name_firm khớp, trả về số tập mới tăng 1
		if (!existingFirms.isEmpty()) {
			int maxPractice = existingFirms.stream().mapToInt(Firm::getPractice).max().orElse(0);
			return String.valueOf(maxPractice + 1);
		}
		// Nếu không tồn tại, trả về 1
		return "1";
	}

	@PostMapping("/check-firm-name-episode")
	@ResponseBody
	public String checkFirmNameBysum(@RequestParam("name_firm") String nameFirm) {
		// Tìm các firm với tên name_firm
		List<Firm> existingFirms = firmRepository.findByName_firm(nameFirm.toLowerCase());

		// Nếu tồn tại firm có name_firm khớp, trả về số tập mới tăng 1
		if (!existingFirms.isEmpty()) {
			int maxPractice = existingFirms.stream().mapToInt(Firm::getTotal_episodes).max().orElse(0);
			return String.valueOf(maxPractice);
		}
		// Nếu không tồn tại, trả về 1
		return "1";
	}

	@PostMapping("/check-episodes-totalEpisodes")
	@ResponseBody
	public Map<String, Object> checkEpisodes(@RequestParam("totalEpisodes") int totalEpisodes,
			@RequestParam("firmName") String firmName) {
		boolean exists = firmService.isPracticeExists(firmName, totalEpisodes);
		Map<String, Object> response = new HashMap<>();
		response.put("exists", exists);
		response.put("message", exists ? "" : "Tập phim này đã có trong cơ sở dữ liệu.");
		return response;
	}

	@GetMapping("/firm/{id}")
	public String getFirmById(@PathVariable("id") Long id, Model model, HttpSession session) {
		Long idUser = (Long) session.getAttribute("id_user");
		Optional<Firm> firmOptional = firmRepository.findById(id);
		model.addAttribute("categoryList", categoryService.getAllCategories());
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

	@GetMapping("/trailer-firm/{id}")
	public String getFirmByIdTraller(@PathVariable("id") Long id, Model model, HttpSession session) {
		model.addAttribute("categoryList", categoryService.getAllCategories());
		Optional<Firm> firmOptional = firmRepository.findById(id);
		if (firmOptional.isPresent()) {
			Firm firm = firmOptional.get();
			firmService.updateStatusFirm(firm.getId());
			model.addAttribute("firm", firm);
			return "web/anime-trailer";

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
		model.addAttribute("categoryList", categoryService.getAllCategories());
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

				firmMovieVideos.put(firm, list);
			}

			model.addAttribute("firmMovieVideos", firmMovieVideos);
		}

		model.addAttribute("firms", firms);
		return "web/test";
	}

	@PostMapping("/clear-success-message")
	public String clearSuccessMessage(HttpSession session) {
		session.removeAttribute("successMessage");
		return "redirect:/forms"; // Redirect về trang hiện tại
	}

}
