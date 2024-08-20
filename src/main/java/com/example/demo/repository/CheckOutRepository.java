package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;

import com.example.demo.model.CheckOut;

@Controller
public interface CheckOutRepository extends JpaRepository<CheckOut, Long> {

}
