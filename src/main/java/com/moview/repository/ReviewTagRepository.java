package com.moview.repository;

import org.springframework.stereotype.Repository;

import com.moview.model.entity.ReviewTag;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewTagRepository {

	private final EntityManager em;

	public ReviewTag save(ReviewTag reviewTag) {
		em.persist(reviewTag);
		return reviewTag;
	}

}
