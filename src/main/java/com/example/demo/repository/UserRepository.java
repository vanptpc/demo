package com.example.demo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.User;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	Optional<User> findById(Long id);

	 @Query("SELECT u FROM User u WHERE u.email = :email AND u.password = :password")
	    Optional<User> login(@Param("email") String email, @Param("password") String password);

}
