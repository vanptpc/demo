package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Comment;
import com.example.demo.model.Firm;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.FirmRepository;
import com.example.demo.repository.UserRepository;

@Service
public class CommentService {

	@Autowired
	UserRepository repository;
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	FirmRepository firmRepository;

	public void saveComment(long id_user, long id_firm, String commnet) {
		Optional<User> optional = repository.findById(id_user);
		Optional<Firm> firms = firmRepository.findById(id_firm);
		Comment comments = new Comment();
		comments.setUser(optional.get());
		comments.setFirm(firms.get());
		comments.setComment(commnet);
		commentRepository.save(comments);
	}

	public List<Comment> findAllByFirmId(Long id_firm) {
		return commentRepository.findAllByFirmId(id_firm);
	}

}
