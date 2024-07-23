package com.moview.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.moview.model.entity.Review;
import com.moview.model.entity.ReviewTag;
import com.moview.repository.ReviewTagRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewTagService {

	private final ReviewTagRepository reviewTagRepository;

	public List<ReviewTag> saveAll(Review review, Optional<List<String>> optionalTags) {

		if(optionalTags.isEmpty()) {
			return new ArrayList<>();
		}

		List<String> tags = optionalTags.get();

		return tags.stream()
			.map(tag -> ReviewTag.of(review, tag))
			.map(reviewTagRepository::save)
			.toList();
	}

	public void deleteAll(Set<ReviewTag> tags) {
		tags.forEach(reviewTagRepository::delete);
	}

}
