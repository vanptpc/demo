package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "firm")
public class Firm {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Tên phim không được để trống")
	private String name_firm;

	@NotNull(message = "Tiêu đề không được để trống")
	private String tittle_firm;

	@NotNull(message = "Tác giả không được để trống")
	private String author_firm;

	private String img_firm;

	private String link_video;

	@NotNull(message = "Coins không được để trống")
	@DecimalMin(value = "0.0", inclusive = false, message = "Coins phải lớn hơn 0")
	private Double coins_video;

	private boolean status;

	@NotNull(message = "Ngày phát hành không được để trống")
	private LocalDateTime firmdate;

	@NotNull(message = "Tập phim không được để trống")
	@Min(value = 1, message = "Tập phim phải lớn hơn hoặc bằng 1")
	private Integer practice;
	private transient String formattedDate;
	@NotNull(message = "Thể loại phim không được để tron  được để trống")
	@ManyToOne
	@JoinColumn(name = "id_category")
	private Category category;
	private String link_video_traller;
}