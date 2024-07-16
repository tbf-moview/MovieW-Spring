package com.moview.repository;

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

	public void delete(ReviewImage reviewImage) {
		em.remove(reviewImage);
	}

}
