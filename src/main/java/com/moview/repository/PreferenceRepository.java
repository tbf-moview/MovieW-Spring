package com.moview.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.moview.model.entity.Member;
import com.moview.model.entity.Preference;
import com.moview.model.entity.Review;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PreferenceRepository {

	private final EntityManager em;

	public Optional<Preference> findByMemberAndReview(Member member, Review review) {

		return em.createQuery("select p from Preference p where p.member = :member and p.review = :review",
				Preference.class)
			.setParameter("member", member)
			.setParameter("review", review)
			.getResultList()
			.stream()
			.findAny();
	}

	public void save(Preference preference) {
		em.persist(preference);
	}

	public long countByReview(Review review) {
		return (long)em.createQuery(
				"select count(p) as like_count from Preference p where p.review = :review and isLike = true")
			.setParameter("review", review)
			.getSingleResult();
	}

}
