package com.moview.repository;

import java.util.Optional;

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

	public Optional<Review> findByIdWithImagesAndTags(Long id) {
		return em.createQuery(
				"select r from Review r left join fetch r.reviewImages left join fetch r.reviewTags where r.id = :id",
				Review.class
			).setParameter("id", id)
			.getResultList()
			.stream()
			.findAny();
	}
}
