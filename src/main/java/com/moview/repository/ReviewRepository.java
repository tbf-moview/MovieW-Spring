package com.moview.repository;

import org.springframework.stereotype.Repository;

import com.moview.model.entity.Review;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewRepository {

	private final EntityManager em;

	public Review save(Review review) {
		em.persist(review);
		return review;
	}

	public Review findById(Long id) {
		return em.find(Review.class, id);
	}

}
