package com.moview.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moview.model.dto.request.ReviewRequestDTO;
import com.moview.model.dto.request.ReviewSearchRequestDTO;
import com.moview.model.dto.response.ReviewResponseDTO;
import com.moview.model.dto.response.ReviewsResponseDTO;
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
    public ResponseEntity<String> createReview(@Validated @ModelAttribute ReviewRequestDTO reviewRequestDTO) {

        log.info("reviewRequestDTO : {}", reviewRequestDTO);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberService.findByEmail(email);

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

        boolean isLikeSign;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (email != null && !email.equals("anonymousUser")) {
            Member member = memberService.findByEmail(email);
            ReviewPreference reviewPreference = reviewPreferenceService.findByMemberAndReview(member, review);
            isLikeSign = reviewPreference.isLikeSign();
        } else {
            isLikeSign = false;
        }


        return ResponseEntity.status(HttpStatus.OK).body(new ReviewResponseDTO(
                review.getId(),
                review.getTitle(),
                review.getContent(),
                review.getMember(),
                review.getReviewTags(),
                review.getCreateDate(),
                review.getUpdateDate(),
                likeCount,
                isLikeSign
        ));
    }

    @PutMapping("/review/{id}")
    public ResponseEntity<String> updateReview(@PathVariable(name = "id") UUID id,
                                               @Validated @ModelAttribute ReviewRequestDTO reviewRequestDTO) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberService.findByEmail(email);

        Review updateReview = reviewService.update(id, member, reviewRequestDTO);
        log.info("updateReview : {}", updateReview);

        return ResponseEntity.status(HttpStatus.OK).body("update complete");
    }

    @DeleteMapping("/review/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable(name = "id") UUID id) {

        Review findReview = reviewService.findByIdWithImagesAndTags(id);

        reviewPreferenceService.deleteAll(findReview);
        reviewService.delete(findReview);

        return ResponseEntity.status(HttpStatus.OK).body("delete complete");
    }

    @PostMapping("/review/{id}/like")
    public ResponseEntity<String> likeReview(@PathVariable(name = "id") UUID id, HttpSession httpSession) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberService.findByEmail(email);
        Review review = reviewService.findByIdWithImagesAndTags(id);

        ReviewPreference reviewPreference = reviewPreferenceService.changePreference(member, review);
        log.info("reviewPreference : {}", reviewPreference);

        return ResponseEntity.status(HttpStatus.OK).body("Change Preference");
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewsResponseDTO>> findAllReviews(@RequestParam(name = "page") int page,
                                                                   @RequestParam(name = "sortOption") String sortOption) {

        List<ReviewsResponseDTO> reviewsResponseDTOS = reviewService.findAllWithLikeCount(sortOption, page);
        return ResponseEntity.status(HttpStatus.OK).body(reviewsResponseDTOS);
    }

    @GetMapping("/reviews/search")
    public ResponseEntity<?> findAll(@ModelAttribute ReviewSearchRequestDTO reviewSearchRequestDTO) {

        log.info("reviewSearchRequestDTO : {}", reviewSearchRequestDTO);

        List<ReviewsResponseDTO> reviewsResponseDTOS = reviewService.search(
                reviewSearchRequestDTO);

        log.info("reviewsResponseDTOs : {}", reviewsResponseDTOS);
        return ResponseEntity.status(HttpStatus.OK).body(reviewsResponseDTOS);
    }
}
