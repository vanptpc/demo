package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Firm;

public interface FirmRepository extends JpaRepository<Firm, Long> {

	@Query("SELECT f FROM Firm f")
	List<Firm> getAllFirms();

	Optional<Firm> findById(Long id);

	@Query("SELECT f FROM MovieFirm mf JOIN mf.firm f GROUP BY f ORDER BY SUM(mf.view_number) DESC")
	List<Firm> getTop5MostViewedFirms();

}