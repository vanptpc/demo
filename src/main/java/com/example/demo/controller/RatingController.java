package com.example.demo.controller;

import com.example.demo.dto.RatingDTO;
import com.example.demo.model.Rating;
import com.example.demo.repository.RatingRepository;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private RatingRepository ratingRepository;
    // Xử lý yêu cầu POST để lưu đánh giá
    @PostMapping
    public ResponseEntity<String> submitRating(@RequestBody RatingDTO ratingRequest) {
        // Tìm đánh giá đã tồn tại cho movieId và userId
        Rating existingRating = ratingRepository.findByMovieIdAndUserId(ratingRequest.getMovieId(), ratingRequest.getUserId());

        if (existingRating != null) {
            // Nếu đã tồn tại, cập nhật đánh giá
            existingRating.setRating(ratingRequest.getRating());
            ratingRepository.save(existingRating);
            return new ResponseEntity<>("Đánh giá đã được cập nhật thành công!", HttpStatus.OK);
        } else {
            // Nếu chưa tồn tại, thêm mới
            Rating newRating = new Rating(ratingRequest.getMovieId(), ratingRequest.getUserId(), ratingRequest.getRating());
            ratingRepository.save(newRating);
            return new ResponseEntity<>("Đánh giá đã được lưu thành công!", HttpStatus.OK);
        }
    }

    // Xử lý yêu cầu GET để lấy đánh giá của người dùng cho một bộ phim
    @GetMapping("/movie/{movieId}/user/{userId}")
    public ResponseEntity<Rating> getRating(@PathVariable Long movieId, @PathVariable Long userId) {
        System.out.println("Received request for movieId: " + movieId + " and userId: " + userId);
        Rating rating = ratingRepository.findByMovieIdAndUserId(movieId, userId);
        if (rating != null) {
            return new ResponseEntity<>(rating, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
