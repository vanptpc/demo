package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Category;
import com.example.demo.model.Firm;

public interface FirmRepository extends JpaRepository<Firm, Long> {

	@Query("SELECT f FROM Firm f WHERE f.status = true")
	List<Firm> getActiveFirms();

	@Query("SELECT f FROM Firm f WHERE f.status = true")
	List<Firm> findAll();

	Optional<Firm> findById(Long id);

	@Query("SELECT f FROM MovieFirm mf JOIN mf.firm f GROUP BY f ORDER BY SUM(mf.view_number) DESC")
	List<Firm> getTop5MostViewedFirms();

	@Query("SELECT LOWER(SUBSTRING(f.name_firm, 1, LOCATE(' ', f.name_firm) - 1)) AS firmName " + "FROM Firm f "
			+ "WHERE f.name_firm LIKE '% %'")
	List<String> getFirmNamesWithCondition();

	@Query("SELECT f FROM Firm f WHERE f.name_firm = :name_firm")
	List<Firm> findByName_firm(String name_firm);

	@Query("SELECT f FROM Firm f WHERE f.name_firm = :name_firm and f.status = true")
	List<Firm> findByName_firmandActive(String name_firm);

	@Query("SELECT COUNT(f) FROM Firm f")
	long countTotalFirms();

	@Query("SELECT f FROM Firm f WHERE f.status = true")
	List<Firm> findByCategory(Category category);

	@Query("SELECT COUNT(f) FROM Firm f WHERE LOWER(f.name_firm) = LOWER(:name_firm)")
	long countByName_firm(@Param("name_firm") String name_firm);

	@Query("SELECT f FROM Firm f WHERE LOWER(f.name_firm) = LOWER(:name_firm) AND f.practice = :practice")
	List<Firm> findByName_firmAndPractice(@Param("name_firm") String name_firm, @Param("practice") Integer practice);
}