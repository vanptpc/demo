//package com.example.demo.service;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.example.demo.model.Rating;
//import com.example.demo.repository.RatingRepository;
//
//@Service
//public class RatingService {
//
//    @Autowired
//    private RatingRepository ratingRepository;
//
//    // Lấy đánh giá trung bình cho một bộ phim
//    public double getAverageRatingByMovieId(Long movieId) {
//        List<Rating> ratings = ratingRepository.findByMovieId(movieId);
//        if (ratings.isEmpty()) {
//            return 0.0;
//        }
//        return ratings.stream().mapToInt(Rating::getRating).average().orElse(0.0);
//    }
//
//    // Lấy đánh giá của một user cho một bộ phim
//    public Integer findByMovieIdAndUserId(Long movieId, Long userId) {
//        Rating rating = ratingRepository.findByMovieIdAndUserId(movieId, userId);
//        return rating != null ? rating.getRating() : null;
//    }
//}
//
//
