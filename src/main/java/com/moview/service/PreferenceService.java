package com.moview.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.moview.model.entity.Member;
import com.moview.model.entity.Preference;
import com.moview.model.entity.Review;
import com.moview.repository.PreferenceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PreferenceService {

	private final PreferenceRepository preferenceRepository;

	public Preference changePreference(Member member, Review review) {

		Optional<Preference> optionalPreference = preferenceRepository.findByMemberAndReview(member, review);

		optionalPreference.ifPresent(Preference::updateLike);

		return optionalPreference.orElseGet(() -> {
			Preference preference = Preference.of(member, review);
			preferenceRepository.save(preference);
			return preference;
		});

	}

	public long countPreference(Review review) {
		return preferenceRepository.countByReview(review);
	}

	public Preference findByMemberAndReview(Member member, Review review) {
		return preferenceRepository.findByMemberAndReview(member, review).orElseGet(Preference::new);
	}

}
