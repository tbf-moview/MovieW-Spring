package com.moview.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moview.model.dto.request.ImageRequestDTO;
import com.moview.model.dto.response.ImageResponseDTO;
import com.moview.model.entity.Member;
import com.moview.model.entity.Review;
import com.moview.model.entity.ReviewImage;
import com.moview.service.MemberService;
import com.moview.service.ReviewImageService;
import com.moview.service.ReviewService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

	private final ReviewImageService reviewImageService;
	private final ReviewService reviewService;
	private final MemberService memberService;

	@PostMapping("/review")
	public ResponseEntity<ImageResponseDTO> ImagesUpload(@RequestBody ImageRequestDTO imageRequestDTO,
		HttpSession httpSession) {

		String email = (String)httpSession.getAttribute("email");
		Member member = memberService.findByEmail(email);

		Review review = Review.of(member, imageRequestDTO.getReviewTitle(), null);
		Review createdReview = reviewService.save(review);

		List<ReviewImage> reviewImages = reviewImageService.saveAll(imageRequestDTO.getImageFiles(), createdReview);

		return ResponseEntity.status(HttpStatus.OK)
			.body(new ImageResponseDTO(
				createdReview.getId(),
				reviewImages.stream()
					.map(ReviewImage::getFileUrl)
					.toList()
			));
	}

}
