package com.moview.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.moview.model.entity.ReviewImage;
import com.moview.repository.ImageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {

	private final ImageRepository imageRepository;

	public void save(ReviewImage reviewImage) {
		imageRepository.save(reviewImage);
	}

	public void saveAll(List<ReviewImage> reviewImages) {
		imageRepository.saveAll(reviewImages);
	}
}
