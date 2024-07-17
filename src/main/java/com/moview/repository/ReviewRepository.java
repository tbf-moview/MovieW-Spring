package com.moview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.moview.model.dto.response.ReviewListResponseDTO;
import com.moview.model.entity.Review;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewRepository {

	private static final int PAGE_CORRECT_FACTOR = -1;

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

	public void delete(Review review) {
		em.remove(review);
	}

	public List<ReviewListResponseDTO> findAllWithLikeCount(int pageNumber, int pageSize) {

		String query = """
			select new com.moview.model.dto.response.ReviewListResponseDTO(
			    r.id,
			    m.email,
			    m.nickname,
			    r.title,
			    r.content,
			    coalesce(count(p), 0),
			    r.createDate,
			    r.updateDate
			)
			from Review r
			left join Member m on r.member.email = m.email
			left join Preference p on r.id = p.review.id
			group by r.id, m.nickname, r.title, r.content, r.createDate, r.updateDate
			order by coalesce(count(p), 0) desc
			""";

		return em.createQuery(query, ReviewListResponseDTO.class)
			.setFirstResult((pageNumber + PAGE_CORRECT_FACTOR) * pageSize)
			.setMaxResults(pageSize)
			.getResultList();
	}
}
