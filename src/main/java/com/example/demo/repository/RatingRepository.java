package com.example.demo.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Rating;



@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

	// Lấy danh sách các đánh giá dựa trên movieId
	Rating findByMovieIdAndUserId(Long movieId, Long userId); // Tìm đánh giá của user cho bộ phim
}
