package com.moview.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moview.model.dto.request.ReviewRequestDTO;
import com.moview.model.dto.response.ReviewListResponseDTO;
import com.moview.model.dto.response.ReviewResponseDTO;
import com.moview.model.entity.Member;
import com.moview.model.entity.Review;
import com.moview.model.entity.ReviewImage;
import com.moview.model.entity.ReviewPreference;
import com.moview.model.vo.ImageVO;
import com.moview.service.MemberService;
import com.moview.service.ReviewImageService;
import com.moview.service.ReviewPreferenceService;
import com.moview.service.ReviewService;
import com.moview.service.ReviewTagService;
import com.moview.service.S3Service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ReviewController {

	private static final String DIR_NAME = "review-images/";

	private final ReviewImageService reviewImageService;
	private final ReviewService reviewService;
	private final MemberService memberService;
	private final ReviewTagService reviewTagService;
	private final ReviewPreferenceService reviewPreferenceService;
	private final S3Service s3Service;

	@PostMapping("/review")
	public ResponseEntity<String> createReview(@Validated @ModelAttribute ReviewRequestDTO reviewRequestDTO,
		HttpSession httpSession) {

		log.info("reviewRequestDTO : {}", reviewRequestDTO);

		String email = (String)httpSession.getAttribute("email");
		Member member = memberService.findByEmail("ee@test.com");

		List<ImageVO> imageVOs = new ArrayList<>();

		try {
			UUID reviewID = UUID.randomUUID();

			imageVOs = s3Service.uploadAll(
				Optional.ofNullable(reviewRequestDTO.getImages()), DIR_NAME, reviewID.toString());

			Review review = reviewService.save(reviewID, member, imageVOs, reviewRequestDTO);
			log.info("review : {}", review);

		} catch (Exception e) {
			imageVOs.forEach(imageVO -> s3Service.delete(imageVO.fileName(), DIR_NAME));
			throw new RuntimeException(e);
		}

		return ResponseEntity.status(HttpStatus.CREATED).body("create complete");
	}

	@GetMapping("/review/{id}")
	public ResponseEntity<ReviewResponseDTO> findReview(@PathVariable(name = "id") UUID id, HttpSession httpSession) {

		Review review = reviewService.findByIdWithImagesAndTags(id);
		log.info("review : {}", review);

		long likeCount = reviewPreferenceService.countPreference(review);
		log.info("likeCount : {}", likeCount);

		String email = (String)httpSession.getAttribute("email");
		Member member = memberService.findByEmail("ee@test.com");

		ReviewPreference reviewPreference = reviewPreferenceService.findByMemberAndReview(member, review);

		return ResponseEntity.status(HttpStatus.OK).body(new ReviewResponseDTO(
			review.getId(),
			review.getTitle(),
			review.getContent(),
			review.getMember(),
			review.getReviewTags(),
			review.getCreateDate(),
			review.getUpdateDate(),
			likeCount,
			reviewPreference.isLikeSign()
		));
	}

	@PutMapping("/review/{id}")
	public ResponseEntity<String> updateReview(@PathVariable(name = "id") UUID id,
		@Validated @ModelAttribute ReviewRequestDTO reviewRequestDTO) {

		Review findReview = reviewService.findByIdWithImagesAndTags(id);
		// reviewImageService.deleteAllAtS3AndDB(findReview.getReviewImages());
		reviewTagService.deleteAll(findReview.getReviewTags());

		List<ImageVO> imageVOs = s3Service.uploadAll(
			Optional.ofNullable(reviewRequestDTO.getImages()), DIR_NAME, String.valueOf(findReview.getId()));

		List<ReviewImage> reviewImages = reviewImageService.saveAll(findReview, imageVOs);
		findReview = reviewService.update(
			findReview,
			reviewRequestDTO.getTitle(),
			reviewRequestDTO.getTexts(),
			reviewImages);

		reviewTagService.saveAll(findReview, Optional.ofNullable(reviewRequestDTO.getTags()));

		return ResponseEntity.status(HttpStatus.OK).body("수정 완료");
	}

	@DeleteMapping("/review/{id}")
	public ResponseEntity<String> deleteReview(@PathVariable(name = "id") UUID id) {

		Review findReview = reviewService.findByIdWithImagesAndTags(id);

		List<String> deletedFileNames = new ArrayList<>();

		try {
			Set<ReviewImage> reviewImages = findReview.getReviewImages();

			for (ReviewImage reviewImage : reviewImages) {
				String deletedFileName = s3Service.delete(reviewImage.getFileName(), DIR_NAME);
				deletedFileNames.add(deletedFileName);
			}

			throw new Exception("testException");

			// reviewService.delete(findReview);

		} catch (Exception e) {

			log.error("error : {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);

			for (String deletedFileName : deletedFileNames) {
				s3Service.rollBack(deletedFileName, DIR_NAME);
			}

			throw new RuntimeException(e);
		}

		// return ResponseEntity.status(HttpStatus.OK).body("delete complete");
	}

	@PutMapping("/review/{id}/like")
	public ResponseEntity<String> likeReview(@PathVariable(name = "id") UUID id, HttpSession httpSession) {

		String email = (String)httpSession.getAttribute("email");
		Member member = memberService.findByEmail("dd@test.com");
		Review review = reviewService.findByIdWithImagesAndTags(id);

		ReviewPreference reviewPreference = reviewPreferenceService.changePreference(member, review);
		log.info("reviewPreference : {}", reviewPreference);

		return ResponseEntity.status(HttpStatus.OK).body("좋아요 변경 완료");
	}

	@GetMapping("/reviews/{page}")
	public ResponseEntity<?> findAllReviews(@PathVariable(name = "page") int page) {

		List<ReviewListResponseDTO> reviewListResponseDTOS = reviewService.findAllWithLikeCount(page);

		return ResponseEntity.status(HttpStatus.OK).body(reviewListResponseDTOS);
	}
}
