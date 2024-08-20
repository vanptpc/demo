package com.example.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.model.Comment;
import com.example.demo.model.Firm;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.FirmRepository;
import com.example.demo.service.CommentService;
import com.example.demo.service.FirmService;

import jakarta.servlet.http.HttpSession;

@Controller
public class VideoController {
	@Autowired
	private FirmRepository firmRepository;
	@Autowired
	FirmService firmService;
	@Autowired
	CommentService commentService;
	@Autowired
	CommentRepository commentRepository;

	@GetMapping("/anime-watching")
	public String video() {
		return "web/anime-watching";

	}

	@GetMapping("/movie_firm/{id}")
	public String getFirmById(@PathVariable("id") Long id, Model model, HttpSession session) {

		Optional<Firm> firmOptional = firmRepository.findById(id);

		if (firmOptional.isPresent()) {
			Firm firm = firmOptional.get();
			session.setAttribute("id_firm", firm.getId());
			model.addAttribute("firm", firm);
			model.addAttribute("videoLink", firm.getLink_video());
			return "redirect:/movie-firm?id=" + firm.getId();
		} else {

			return "redirect:/";
		}
	}

	@GetMapping("/movie-firm")
	public String movieSuccess(@RequestParam("id") long firmId, HttpSession session, Model model) {

		Boolean islogin = (Boolean) session.getAttribute("islogin");

		if (islogin == null) {
			islogin = false;
		}

		session.setAttribute("islogin", true);

		// Lấy thông tin của firm dựa trên firmId để hiển thị chi tiết
		Optional<Firm> firmOptional = firmRepository.findById(firmId);
		if (firmOptional.isPresent()) {
			Firm firm = firmOptional.get();
			List<Comment> comments = commentRepository.findByFirmIdWithUser(firm.getId());

			model.addAttribute("firm", firm);
			model.addAttribute("comments", comments);
			model.addAttribute("videoLink", firm.getLink_video());
		}

		return "web/anime-watching";
	}

	@PostMapping("/submit-comment")
	@ResponseBody
	public Map<String, Object> submitComment(@RequestParam("comment") String commentText, HttpSession session) {

		Long userId = (Long) session.getAttribute("id_user");
		Long id_firm = (Long) session.getAttribute("id_firm");

		if (userId == null) {
			return Map.of("status", "error", "message", "User not logged in");
		}

		commentService.saveComment(userId, id_firm, commentText);

		// Returning the comment as a JSON response
		return Map.of("status", "success", "commentText", commentText);
	}

	@GetMapping("/comments")
	@ResponseBody
	public List<Comment> getComments(@RequestParam("id_firm") Long id_firm) {
		return commentService.findAllByFirmId(id_firm);
	}
}
