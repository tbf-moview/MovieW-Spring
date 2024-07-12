package com.moview.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.moview.model.entity.ReviewImage;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ImageRepository {

	private final EntityManager em;

	public void save(ReviewImage reviewImage) {
		em.persist(reviewImage);
	}

	public void saveAll(List<ReviewImage> reviewImages) {

		try {
			em.getTransaction().begin();
			for (ReviewImage reviewImage : reviewImages) {
				em.persist(reviewImage);
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

}
