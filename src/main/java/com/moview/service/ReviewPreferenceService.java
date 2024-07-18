package com.moview.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.moview.model.entity.Member;
import com.moview.model.entity.ReviewPreference;
import com.moview.model.entity.Review;
import com.moview.repository.PreferenceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewPreferenceService {

	private final PreferenceRepository preferenceRepository;

	public ReviewPreference changePreference(Member member, Review review) {

		Optional<ReviewPreference> optionalPreference = preferenceRepository.findByMemberAndReview(member, review);

		optionalPreference.ifPresent(ReviewPreference::updateLike);

		return optionalPreference.orElseGet(() -> {
			ReviewPreference reviewPreference = ReviewPreference.of(member, review);
			preferenceRepository.save(reviewPreference);
			return reviewPreference;
		});

	}

	public long countPreference(Review review) {
		return preferenceRepository.countByReview(review);
	}

	public ReviewPreference findByMemberAndReview(Member member, Review review) {
		return preferenceRepository.findByMemberAndReview(member, review).orElseGet(ReviewPreference::new);
	}

}
