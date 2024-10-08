package com.moview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.moview.model.entity.Member;
import com.moview.model.entity.ReviewPreference;
import com.moview.model.entity.Review;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewPreferenceRepository {

	private final EntityManager em;

	public Optional<ReviewPreference> findByMemberAndReview(Member member, Review review) {

		return em.createQuery("select p from ReviewPreference p where p.member = :member and p.review = :review",
				ReviewPreference.class)
			.setParameter("member", member)
			.setParameter("review", review)
			.getResultList()
			.stream()
			.findAny();
	}

	public void save(ReviewPreference reviewPreference) {
		em.persist(reviewPreference);
	}

	public long countByReview(Review review) {
		return em.createQuery(
				"select count(p) as like_count from ReviewPreference p where p.review = :review and p.likeSign = TRUE", Long.class)
			.setParameter("review", review)
			.getSingleResult();
	}

	public List<ReviewPreference> findAll(Review review) {

		return em.createQuery("select p from ReviewPreference p where p.review = :review", ReviewPreference.class)
			.setParameter("review", review)
			.getResultList();
	}

	public void delete(ReviewPreference reviewPreference) {
		em.remove(reviewPreference);
	}
}
