package com.moview.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.moview.model.entity.Review;
import com.moview.model.entity.ReviewImage;
import com.moview.model.vo.ImageVO;
import com.moview.repository.ReviewImageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewImageService {

	private final ReviewImageRepository reviewImageRepository;
	private final S3Service s3Service;

	public List<ReviewImage> saveAll(Review review, List<ImageVO> images) {

		return images.stream()
			.map(imageVO -> reviewImageRepository.save(ReviewImage.of(review, imageVO.fileName(), imageVO.fileUrl())))
			.toList();

	}

	public void deleteAllAtS3AndDB(Set<ReviewImage> images) {

		// Todo: S3 삭제 트랜잭션 처리
		images.forEach(this::deleteS3AndDBReviewImages);
	}

	private void deleteS3AndDBReviewImages(ReviewImage reviewImage) {
		reviewImageRepository.delete(reviewImage);
		s3Service.deleteS3File(reviewImage.getFileName());
	}
}
