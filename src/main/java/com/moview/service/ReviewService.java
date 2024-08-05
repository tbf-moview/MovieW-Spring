package com.moview.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.moview.common.ErrorMessage;
import com.moview.model.dto.request.ReviewRequestDTO;
import com.moview.model.dto.request.ReviewSearchRequestDTO;
import com.moview.model.dto.response.ReviewsResponseDTO;
import com.moview.model.entity.Member;
import com.moview.model.entity.Review;
import com.moview.model.entity.ReviewImage;
import com.moview.model.vo.ImageVO;
import com.moview.repository.ReviewRepository;
import com.moview.util.FileManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

	public static final String DIR_NAME = "review-images/";
	private static final int PAGE_SIZE = 20;

	private final ReviewRepository reviewRepository;
	private final ReviewTransactionService reviewTransactionService;
	private final S3Service s3Service;

	public Review save(Member member, ReviewRequestDTO reviewRequestDTO) {

		List<ImageVO> imageVOs = new ArrayList<>();

		try {
			UUID reviewID = UUID.randomUUID();

			imageVOs = s3Service.uploadAll(
				Optional.ofNullable(reviewRequestDTO.getImages()), DIR_NAME, reviewID.toString());

			Review review = reviewTransactionService.save(reviewID, member, imageVOs, reviewRequestDTO);
			log.info("review : {}", review);

			return review;

		} catch (AmazonClientException amazonClientException) {
			imageVOs.forEach(imageVO -> s3Service.delete(imageVO.fileName(), DIR_NAME));
			throw new AmazonClientException(amazonClientException.getMessage(), amazonClientException);

		} catch (RuntimeException runtimeException) {
			imageVOs.forEach(imageVO -> s3Service.delete(imageVO.fileName(), DIR_NAME));
			throw new RuntimeException(runtimeException.getMessage(), runtimeException);
		}
	}

	public Review findByIdWithImagesAndTags(UUID id) {
		return reviewRepository.findByIdWithImagesAndTags(id)
			.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.REVIEW_NOT_EXIST));
	}

	public List<ReviewsResponseDTO> findAllWithLikeCount(int pageNumber) {
		return reviewRepository.findAllWithLikeCount(pageNumber, PAGE_SIZE);
	}

	public List<ReviewsResponseDTO> findBySearchWordWithLikeCount(ReviewSearchRequestDTO reviewSearchRequestDTO) {
		return reviewRepository.findBySearchWordWithLikeCount(reviewSearchRequestDTO, PAGE_SIZE);
	}

	public void delete(Review findReview) {

		List<String> deletedFileNames = new ArrayList<>();

		try {

			reviewTransactionService.delete(findReview);

			Set<ReviewImage> reviewImages = findReview.getReviewImages();

			for (ReviewImage reviewImage : reviewImages) {
				String deletedFileName = s3Service.delete(reviewImage.getFileName(), DIR_NAME);
				deletedFileNames.add(deletedFileName);
			}

		} catch (AmazonClientException amazonClientException) {

			for (String deletedFileName : deletedFileNames) {
				s3Service.rollBack(deletedFileName, DIR_NAME);
			}

			throw new AmazonClientException(amazonClientException.getMessage(), amazonClientException);
		}
	}

	public Review update(UUID reviewID, Member member, ReviewRequestDTO reviewRequestDTO) {

		Review review = findByIdWithImagesAndTags(reviewID);

		List<String> extractFileNames = FileManager.extractImageFileNameInContent(reviewRequestDTO.getTexts());
		List<String> originalFileNames = review.getReviewImages()
			.stream()
			.map(ReviewImage::getFileName)
			.toList();

		List<String> deletedFiles = new ArrayList<>();
		List<ImageVO> imageVOs = new ArrayList<>();

		try {

			for (String originalFileName : originalFileNames) {

				if (!extractFileNames.contains(originalFileName)) {
					s3Service.delete(originalFileName, DIR_NAME);
					deletedFiles.add(originalFileName);
				}
			}

			imageVOs = s3Service.uploadAll(
				Optional.ofNullable(reviewRequestDTO.getImages()), DIR_NAME, review.getId().toString());

			Review updateReview = reviewTransactionService.update(review.getId(), member, imageVOs, reviewRequestDTO,
				deletedFiles);
			log.info("updateReview : {}", updateReview);

			return updateReview;

		} catch (AmazonClientException amazonClientException) {

			deletedFiles.forEach(deletedFile -> s3Service.rollBack(deletedFile, DIR_NAME));
			imageVOs.forEach(imageVO -> s3Service.delete(imageVO.fileName(), DIR_NAME));

			throw new RuntimeException(amazonClientException.getMessage(), amazonClientException);
		}

	}

}
