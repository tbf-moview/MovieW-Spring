package com.moview.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.moview.common.ErrorMessage;
import com.moview.model.dto.request.ReviewRequestDTO;
import com.moview.model.dto.response.ReviewListResponseDTO;
import com.moview.model.entity.Member;
import com.moview.model.entity.Review;
import com.moview.model.entity.ReviewImage;
import com.moview.model.entity.ReviewTag;
import com.moview.model.vo.ImageVO;
import com.moview.repository.ReviewRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewService {

	private static final int PAGE_SIZE = 20;
	private static final String START_IMAGE_TAG = "<img src=\"";
	private static final String END_IMAGE_TAG = "\"/>";

	private final ReviewRepository reviewRepository;

	public Review save(UUID reviewID, Member member, List<ImageVO> imageVOs,
		ReviewRequestDTO reviewRequestDTO) {

		Review review = Review.of(reviewID, member, reviewRequestDTO.getTitle());

		List<ReviewImage> reviewImages = makeReviewImages(review, Optional.ofNullable(imageVOs));
		Set<ReviewTag> reviewTags = makeReviewTags(review, Optional.ofNullable(reviewRequestDTO.getTags()));

		String content = makeContent(reviewRequestDTO.getTexts(), reviewImages);
		review.updateContent(content);

		review.addReviewImages(reviewImages);
		review.addReviewTags(reviewTags);

		return reviewRepository.save(review);
	}

	private List<ReviewImage> makeReviewImages(Review review, Optional<List<ImageVO>> optionalImageVOs) {

		return optionalImageVOs.map(imageVOS -> imageVOS
			.stream()
			.map(imageVO -> ReviewImage.of(review, imageVO.fileName(), imageVO.fileUrl()))
			.toList()).orElseGet(ArrayList::new);

	}

	private Set<ReviewTag> makeReviewTags(Review review, Optional<List<String>> optionalTags) {

		return optionalTags.map(strings -> strings.stream()
			.map(tag -> ReviewTag.of(review, tag))
			.collect(Collectors.toSet())).orElseGet(LinkedHashSet::new);

	}

	public String makeContent(List<String> texts, List<ReviewImage> reviewImages) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < reviewImages.size(); i++) {
			stringBuilder.append(texts.get(i));
			stringBuilder.append(START_IMAGE_TAG).append(reviewImages.get(i).getFileUrl()).append(END_IMAGE_TAG);
		}

		return stringBuilder.append(texts.getLast()).toString();
	}

	public Review update(Review review, String title, List<String> texts, List<ReviewImage> reviewImages) {
		String content = makeContent(texts, reviewImages);
		review.updateContent(content);
		return review;
	}

	public Review findByIdWithImagesAndTags(Long id) {
		return reviewRepository.findByIdWithImagesAndTags(id)
			.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.REVIEW_NOT_EXIST));
	}

	public void delete(Review review) {
		reviewRepository.delete(review);
	}

	public List<ReviewListResponseDTO> findAllWithLikeCount(int pageNumber) {
		return reviewRepository.findAllWithLikeCount(pageNumber, PAGE_SIZE);
	}

}
