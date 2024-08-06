package com.moview.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.moview.model.constant.SearchOption;
import com.moview.model.dto.response.ReviewsResponseDTO;
import com.moview.model.entity.Review;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewRepository {

	private static final int PAGE_CORRECT_FACTOR = -1;

	private final EntityManager em;

	public Review save(Review review) {
		em.persist(em.merge(review));
		return review;
	}

	public Optional<Review> findByIdWithImagesAndTags(UUID id) {
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

	public List<ReviewsResponseDTO> findAllWithLikeCount(int pageNumber, int pageSize) {

		String query = """
			select new com.moview.model.dto.response.ReviewsResponseDTO(
			    r.id,
			    m.email,
			    m.nickname,
			    r.title,
			    r.content,
			    coalesce(count(case when p.likeSign = true then 1 else null end), 0),
			    r.createDate,
			    r.updateDate
			)
			from Review r
			left join Member m on r.member.email = m.email
			left join ReviewPreference p on r.id = p.review.id
			group by r.id, m.nickname, r.title, r.content, r.createDate, r.updateDate
			order by coalesce(count(case when p.likeSign = true then 1 else null end), 0) desc, r.createDate desc
			""";

		return em.createQuery(query, ReviewsResponseDTO.class)
			.setFirstResult((pageNumber + PAGE_CORRECT_FACTOR) * pageSize)
			.setMaxResults(pageSize)
			.getResultList();
	}

	public List<ReviewsResponseDTO> findBySearchWordWithLikeCount(String searchOption, String searchWord, int pageNum,
		int pageSize) {

		String query = makeBaseQuery() + makeWhereClause(searchOption) + makeGroupAndOrderByClause();

		return em.createQuery(query, ReviewsResponseDTO.class)
			.setParameter("searchWord", searchWord)
			.setFirstResult((pageNum + PAGE_CORRECT_FACTOR) * pageSize)
			.setMaxResults(pageSize)
			.getResultList();
	}

	private String makeBaseQuery() {
		return """
			select new com.moview.model.dto.response.ReviewsResponseDTO(
			    r.id,
			    m.email,
			    m.nickname,
			    r.title,
			    r.content,
			    coalesce(count(case when p.likeSign = true then 1 else null end), 0) as likeSign,
			    r.createDate,
			    r.updateDate
			)
			from Review r
			join Member m on r.member.email = m.email
			left join ReviewPreference p on r.id = p.review.id
			""";
	}

	private String makeWhereClause(String searchOption) {

		return switch (SearchOption.valueOf(searchOption.toUpperCase())) {
			case SearchOption.NICKNAME -> "where lower(m.nickname) like lower(concat('%', :searchWord, '%'))";
			case SearchOption.TITLE -> "where lower(r.title) like lower(concat('%', :searchWord, '%'))";
			case SearchOption.CONTENT -> "where lower(r.content) like lower(concat('%', :searchWord, '%'))";
			case SearchOption.TAG ->
				"where r.id IN (select rt.review.id from ReviewTag rt where lower(rt.tag) = lower(:searchWord))";
		};

	}

	private String makeGroupAndOrderByClause() {
		return "group by r.id, m.email, m.nickname, r.title, r.content, r.createDate, r.updateDate order by likeSign desc, r.createDate desc";
	}

}
