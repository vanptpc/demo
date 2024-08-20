package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public boolean checkEmail(String email) {
		Optional<User> optional = userRepository.findByEmail(email);
		if (optional.isPresent())
			return true;
		else
			return false;
	}

	public long saveUser(String email, String username, String password) {
		User user = new User();
		user.setEmail(email);
		user.setName(username);
		user.setPassword(password);
		userRepository.save(user);
		return user.getId();
	}

	public Optional<User> checkLogin(String email, String password) {

		Optional<User> users = userRepository.login(email, password);
		return users;
	}
}
