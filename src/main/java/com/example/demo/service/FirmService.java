package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Firm;
import com.example.demo.repository.FirmRepository;

@Service
public class FirmService {

	@Autowired
	private FirmRepository firmRepository;

	public void updateStatusFirm(long id) {
		Optional<Firm> optional = firmRepository.findById(id);
		Firm firm = optional.get();
		firm.setStatus(true);
		firmRepository.save(firm);
	}

	public List<Firm> getTop5MostViewedFirms() {
		return firmRepository.getTop5MostViewedFirms();
	}
}
