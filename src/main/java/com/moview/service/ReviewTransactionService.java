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
public class ReviewTransactionService {

	private static final String START_IMAGE_TAG = "<img src=\"";
	private static final String END_IMAGE_TAG = "\">";

	private final ReviewRepository reviewRepository;

	@Transactional
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

	private String makeContent(List<String> texts, List<ReviewImage> reviewImages) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < reviewImages.size(); i++) {
			stringBuilder.append(texts.get(i));
			stringBuilder.append(START_IMAGE_TAG).append(reviewImages.get(i).getFileUrl()).append(END_IMAGE_TAG);
		}

		return stringBuilder.append(texts.getLast()).toString();
	}

	@Transactional
	public void delete(Review review) {
		reviewRepository.delete(review);
	}

	@Transactional
	public Review update(UUID reviewID, Member member, List<ImageVO> imageVOs, ReviewRequestDTO reviewRequestDTO,
		List<String> deleteFilenames) {

		Optional<Review> optionalReview = reviewRepository.findByIdWithImagesAndTags(reviewID);

		Review review = optionalReview.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.REVIEW_NOT_EXIST));

		if (!review.getMember().equals(member)) {
			throw new IllegalArgumentException("[ERROR] 작성 권한이 없는 회원입니다.]");
		}

		List<ReviewImage> newReviewImages = makeReviewImages(review, Optional.ofNullable(imageVOs));

		String content = makeContent(reviewRequestDTO.getTexts(), newReviewImages);
		review.updateContent(content);
		review.updateTitle(reviewRequestDTO.getTitle());

		updateReviewImages(review, deleteFilenames, newReviewImages);
		updateReviewTags(review, reviewRequestDTO.getTags());

		return review;

	}

	private void updateReviewImages(Review review, List<String> deleteFileNames,
		List<ReviewImage> newReviewImages) {

		Set<ReviewImage> originalImages = review.getReviewImages();

		List<ReviewImage> deleteReviewImages = originalImages.stream()
			.filter(originalImage -> deleteFileNames.contains(originalImage.getFileName()))
			.toList();

		review.deleteReviewImages(deleteReviewImages);
		review.addReviewImages(newReviewImages);

	}

	private void updateReviewTags(Review review, List<String> tags) {

		Set<ReviewTag> originalReviewTags = review.getReviewTags();
		Set<ReviewTag> newReviewTags = tags.stream()
			.map(tag -> ReviewTag.of(review, tag))
			.collect(Collectors.toSet());

		Set<ReviewTag> deleteTags = originalReviewTags.stream()
			.filter(originalTag -> !newReviewTags.contains(originalTag))
			.collect(Collectors.toSet());

		System.out.println("deleteTags = " + deleteTags);

		Set<ReviewTag> addTags = newReviewTags.stream()
			.filter(newTag -> !originalReviewTags.contains(newTag))
			.collect(Collectors.toSet());

		System.out.println("addTags = " + addTags);

		review.deleteReviewTags(deleteTags);
		review.addReviewTags(addTags);
	}

}
