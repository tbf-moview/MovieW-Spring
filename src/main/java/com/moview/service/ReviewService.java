package com.moview.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.moview.model.dto.response.ReviewListResponseDTO;
import com.moview.model.entity.Member;
import com.moview.model.entity.Review;
import com.moview.model.entity.ReviewImage;
import com.moview.repository.ReviewRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

	private static final int PAGE_SIZE = 20;

	private final ReviewRepository reviewRepository;

	public Review save(Member member, String title) {
		Review review = Review.of(member, title, null);
		return reviewRepository.save(review);
	}

	public Review update(Review review, String title, List<String> texts, List<ReviewImage> reviewImages) {
		String content = makeContent(texts, reviewImages);
		review.update(title, content);
		return review;
	}

	public String makeContent(List<String> texts, List<ReviewImage> reviewImages) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < reviewImages.size(); i++) {

			stringBuilder.append(texts.get(i));
			stringBuilder.append("<img src=\"").append(reviewImages.get(i).getFileUrl()).append("\"/>");
		}
		return stringBuilder.append(texts.getLast()).toString();
	}

	public Review findByIdWithImagesAndTags(Long id) {
		return reviewRepository.findByIdWithImagesAndTags(id)
			.orElseThrow(() -> new IllegalArgumentException("[ERROR]존재하지 않는 리뷰"));
	}

	public void delete(Review review) {
		reviewRepository.delete(review);
	}

	public List<ReviewListResponseDTO> findAllWithLikeCount(int pageNumber) {
		return reviewRepository.findAllWithLikeCount(pageNumber, PAGE_SIZE);
	}

}
