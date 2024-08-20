package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;

import com.example.demo.model.MovieVideo;

@Repository
public interface MovieVideoRepository extends JpaRepository<MovieVideo, Long> {
	@Query("SELECT mv FROM MovieVideo mv WHERE mv.user.id = :userId AND mv.firm.id = :firmId")
	List<MovieVideo> findByUserIdAndFirmId(@Param("userId") Long userId, @Param("firmId") Long firmId);

	@Query("SELECT CASE WHEN COUNT(mv) > 0 THEN true ELSE false END FROM MovieVideo mv WHERE mv.user.id = :userId AND mv.firm.id = :firmId")
	boolean existsByUserIdAndFirmId(@Param("userId") long userId, @Param("firmId") long firmId);

	@Query("SELECT mv FROM MovieVideo mv WHERE mv.user.id = :userId AND mv.firm.id = :firmId")
	Optional<MovieVideo> findById(@Param("userId") Long userId, @Param("firmId") Long firmId);
}
