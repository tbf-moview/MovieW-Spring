package com.moview.controller;

import java.util.List;

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
import com.moview.model.dto.response.ReviewResponseDTO;
import com.moview.model.entity.Member;
import com.moview.model.entity.Review;
import com.moview.model.entity.ReviewImage;
import com.moview.service.MemberService;
import com.moview.service.ReviewImageService;
import com.moview.service.ReviewService;
import com.moview.service.ReviewTagService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ReviewController {

	private final ReviewImageService reviewImageService;
	private final ReviewService reviewService;
	private final MemberService memberService;
	private final ReviewTagService reviewTagService;

	@PostMapping("/review")
	public ResponseEntity<String> createReview(@Validated @ModelAttribute ReviewRequestDTO reviewRequestDTO,
		HttpSession httpSession) {

		log.info("reviewRequestDTO : {}", reviewRequestDTO);

		String email = (String)httpSession.getAttribute("email");
		Member member = memberService.findByEmail(email);

		// Todo: 리뷰 작성 중 오류 일어날 시, 생성된 리뷰 제거하는 로직
		Review createdReview = reviewService.save(member, reviewRequestDTO.getTitle());
		List<ReviewImage> reviewImages = reviewImageService.saveAll(reviewRequestDTO.getImages(), createdReview);
		createdReview = reviewService.update(createdReview, reviewRequestDTO.getTitle(), reviewRequestDTO.getTexts(),
			reviewImages);
		reviewTagService.saveAll(createdReview, reviewRequestDTO.getTags());

		log.info("createdReview : {}", createdReview.toString());

		return ResponseEntity.status(HttpStatus.CREATED).body("작성 완료");
	}

	@GetMapping("/review/{id}")
	public ResponseEntity<ReviewResponseDTO> findReview(@PathVariable(name = "id") Long id) {
		Review review = reviewService.findByIdWithImagesAndTags(id);
		log.info("review : {}", review);
		return ResponseEntity.status(HttpStatus.OK).body(new ReviewResponseDTO(
			review.getId(),
			review.getTitle(),
			review.getContent(),
			review.getMember(),
			review.getReviewTags(),
			review.getCreateDate(),
			review.getUpdateDate()
		));
	}

	@PutMapping("/review/{id}")
	public ResponseEntity<?> updateReview(@PathVariable(name = "id") Long id,
		@Validated @ModelAttribute ReviewRequestDTO reviewRequestDTO) {

		Review findReview = reviewService.findByIdWithImagesAndTags(id);
		reviewImageService.deleteAll(findReview.getReviewImages());
		reviewTagService.deleteAll(findReview.getReviewTags());

		List<ReviewImage> reviewImages = reviewImageService.saveAll(reviewRequestDTO.getImages(), findReview);
		reviewService.update(findReview, reviewRequestDTO.getTitle(), reviewRequestDTO.getTexts(), reviewImages);
		reviewTagService.saveAll(findReview, reviewRequestDTO.getTags());

		return ResponseEntity.status(HttpStatus.OK).body("수정 완료");
	}

	@DeleteMapping("/review/{id}")
	public ResponseEntity<?> deleteReview(@PathVariable(name = "id") Long id) {

		Review findReview = reviewService.findByIdWithImagesAndTags(id);
		reviewImageService.deleteAll(findReview.getReviewImages());
		reviewTagService.deleteAll(findReview.getReviewTags());
		reviewService.delete(findReview);

		return ResponseEntity.status(HttpStatus.OK).body("삭제 완료");
	}

}
