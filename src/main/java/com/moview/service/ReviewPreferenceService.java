package com.moview.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.moview.model.entity.Member;
import com.moview.model.entity.ReviewPreference;
import com.moview.model.entity.Review;
import com.moview.repository.ReviewPreferenceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewPreferenceService {

	private final ReviewPreferenceRepository reviewPreferenceRepository;

	public ReviewPreference changePreference(Member member, Review review) {

		Optional<ReviewPreference> optionalPreference = reviewPreferenceRepository.findByMemberAndReview(member, review);

		optionalPreference.ifPresent(ReviewPreference::updateLike);

		return optionalPreference.orElseGet(() -> {
			ReviewPreference reviewPreference = ReviewPreference.of(member, review);
			reviewPreferenceRepository.save(reviewPreference);
			return reviewPreference;
		});

	}

	public long countPreference(Review review) {
		return reviewPreferenceRepository.countByReview(review);
	}

	public ReviewPreference findByMemberAndReview(Member member, Review review) {
		return reviewPreferenceRepository.findByMemberAndReview(member, review).orElseGet(ReviewPreference::new);
	}

	public void deleteAll(Review review) {

		List<ReviewPreference> reviewPreferences = reviewPreferenceRepository.findAllByReview(review);

		if(reviewPreferences.isEmpty()) {
			return;
		}

		reviewPreferences.forEach(reviewPreferenceRepository::delete);
	}
}
