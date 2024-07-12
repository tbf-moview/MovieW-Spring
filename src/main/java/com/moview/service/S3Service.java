package com.moview.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.moview.model.entity.Review;
import com.moview.model.entity.ReviewImage;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3Service {

	private final AmazonS3 amazonS3;
	private final String bucket;

	public S3Service(AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket}") String bucket) {
		this.amazonS3 = amazonS3;
		this.bucket = bucket;
	}

	public List<ReviewImage> upload(List<MultipartFile> files, Review review) {

		List<ReviewImage> reviewImages = new ArrayList<>();

		try {
			for (MultipartFile file : files) {
				ReviewImage upload = upload(file, review);
				reviewImages.add(upload);
			}

		} catch (IOException e) {
			for (ReviewImage reviewImage : reviewImages) {
				deleteFile(reviewImage.getFileName());
			}
		}

		return reviewImages;
	}

	public ReviewImage upload(MultipartFile file, Review review) throws IOException {

		String originalFilename = file.getOriginalFilename();
		log.info("originalFilename : {}", originalFilename);

		String uploadedFilename =
			"test/" + review.getId() + "_" + UUID.randomUUID() + "_" + Objects.requireNonNull(originalFilename)
				.replaceAll("\\s", "_");

		File uploadFile = new File(uploadedFilename);

		File parentDir = uploadFile.getParentFile();
		if (parentDir != null && !parentDir.exists()) {
			if (!parentDir.mkdirs()) {
				throw new IOException("디렉토리를 생성할 수 없습니다: " + parentDir);
			}
		}

		try (FileOutputStream fileOutputStream = new FileOutputStream(uploadFile)) {
			fileOutputStream.write(file.getBytes());
		}

		log.info("uploadedFilename : {}", uploadedFilename);

		amazonS3.putObject(new PutObjectRequest(bucket, uploadedFilename, uploadFile)
			.withCannedAcl(CannedAccessControlList.PublicRead));

		String uploadUrl = URLEncoder.encode(amazonS3.getUrl(bucket, uploadedFilename).toString(),
			StandardCharsets.UTF_8);

		if (uploadFile.delete()) {
			log.info("{} 삭제완료", uploadedFilename);
		} else {
			log.info("{} 삭제실패", uploadedFilename);
		}

		return ReviewImage.of(review, uploadedFilename, uploadUrl);
	}

	public void deleteFile(String fileName) {

		String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);

		amazonS3.deleteObject(bucket, decodedFileName);
		log.info("delete file {}", decodedFileName);

	}

}
