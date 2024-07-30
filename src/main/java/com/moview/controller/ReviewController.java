package com.moview.controller;

import java.util.List;
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
import com.moview.model.entity.ReviewPreference;
import com.moview.service.MemberService;
import com.moview.service.ReviewPreferenceService;
import com.moview.service.ReviewService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ReviewController {

	private final ReviewService reviewService;
	private final MemberService memberService;
	private final ReviewPreferenceService reviewPreferenceService;

	@PostMapping("/review")
	public ResponseEntity<String> createReview(@Validated @ModelAttribute ReviewRequestDTO reviewRequestDTO,
		HttpSession httpSession) {

		log.info("reviewRequestDTO : {}", reviewRequestDTO);

		String email = (String)httpSession.getAttribute("email");
		Member member = memberService.findByEmail("ee@test.com");

		Review saveReview = reviewService.save(member, reviewRequestDTO);
		log.info("saveReview : {}", saveReview);

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

		Member member = memberService.findByEmail("ee@test.com");

		Review updateReview = reviewService.update(id, member, reviewRequestDTO);
		log.info("updateReview : {}", updateReview);

		return ResponseEntity.status(HttpStatus.OK).body("update complete");
	}

	@DeleteMapping("/review/{id}")
	public ResponseEntity<String> deleteReview(@PathVariable(name = "id") UUID id) {

		reviewService.delete(id);
		return ResponseEntity.status(HttpStatus.OK).body("delete complete");
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
	public ResponseEntity<List<ReviewListResponseDTO>> findAllReviews(@PathVariable(name = "page") int page) {

		List<ReviewListResponseDTO> reviewListResponseDTOS = reviewService.findAllWithLikeCount(page);

		return ResponseEntity.status(HttpStatus.OK).body(reviewListResponseDTOS);
	}
}
