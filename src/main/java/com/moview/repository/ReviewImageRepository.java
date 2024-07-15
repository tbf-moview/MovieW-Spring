package com.moview.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.moview.model.entity.ReviewImage;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewImageRepository {

	private final EntityManager em;

	public void save(ReviewImage reviewImage) {
		em.persist(reviewImage);
	}

	public void saveAll(List<ReviewImage> reviewImages) {
		reviewImages.forEach(this::save);
	}

	public void delete(ReviewImage reviewImage) {
		em.remove(reviewImage);
	}

}
