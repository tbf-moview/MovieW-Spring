package com.moview.service;

import org.springframework.stereotype.Service;

import com.moview.model.entity.Review;
import com.moview.repository.ReviewRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

	private final ReviewRepository reviewRepository;

	public Review save(Review review) {
		return reviewRepository.save(review);
	}

}
