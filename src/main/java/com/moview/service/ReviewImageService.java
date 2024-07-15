package com.moview.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.moview.model.entity.Review;
import com.moview.model.entity.ReviewImage;
import com.moview.model.vo.Image;
import com.moview.repository.ReviewImageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewImageService {

	private static final String DIR_NAME = "review-images/";

	private final ReviewImageRepository reviewImageRepository;
	private final S3Service s3Service;

	public List<ReviewImage> saveAll(List<MultipartFile> originalFiles, Review review) {

		List<ReviewImage> images = new ArrayList<>();

		try {
			for (MultipartFile originalFile : originalFiles) {
				ReviewImage reviewImage = uploadS3AndSaveDBReviewImage(review, originalFile);
				images.add(reviewImage);
			}

		} catch (IOException e) {
			deleteUploadReviewImage(images);
			throw new RuntimeException(e);
		}

		return images;
	}

	private ReviewImage uploadS3AndSaveDBReviewImage(Review review, MultipartFile originalFile) throws IOException {
		Image uploadImage = s3Service.upload(originalFile, DIR_NAME, String.valueOf(review.getId()));
		ReviewImage reviewImage = ReviewImage.of(review, uploadImage.fileName(), uploadImage.fileUrl());
		reviewImageRepository.save(reviewImage);
		return reviewImage;
	}

	private void deleteUploadReviewImage(List<ReviewImage> images) {
		for (ReviewImage image : images) {
			s3Service.deleteS3File(image.getFileName());
		}
	}

	public void deleteAll(Set<ReviewImage> images) {
		images.forEach(this::deleteS3AndDBReviewImages);
	}

	private void deleteS3AndDBReviewImages(ReviewImage reviewImage) {
		reviewImageRepository.delete(reviewImage);
		s3Service.deleteS3File(reviewImage.getFileName());
	}

}
