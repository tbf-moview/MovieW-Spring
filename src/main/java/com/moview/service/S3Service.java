package com.moview.service;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.moview.model.entity.ReviewImage;
import com.moview.model.vo.Image;
import com.moview.util.FileConverter;

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

	public Image upload(MultipartFile file, String dirName, String prefixName) throws IOException {

		File uploadFile = FileConverter.convertFile(file, prefixName);
		String uploadFilename = dirName + uploadFile.getName();
		log.info("Uploaded file name : {}", uploadFilename);

		try {
			uploadToS3(uploadFile, uploadFilename);

		} catch (Exception e) {
			log.error("파일 업로드 중 예외 발생 : {}", e.getMessage(), e);
			throw e;

		} finally {
			deleteFile(uploadFile);
		}

		String url = amazonS3.getUrl(bucket, uploadFilename).toString();

		return new Image(uploadFilename, url);
	}

	private void deleteFile(File file) {

		if (file.delete()) {
			log.info("{} 삭제완료", file.getName());
		} else {
			log.info("{} 삭제실패", file.getName());
		}
	}

	private void uploadToS3(File file, String fileName) {

		amazonS3.putObject(new PutObjectRequest(bucket, fileName, file)
			.withCannedAcl(CannedAccessControlList.PublicRead));
	}

	public void deleteS3File(String fileName) {

		// Todo: 디코딩 필요한지 확인
		String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
		amazonS3.deleteObject(bucket, decodedFileName);
		log.info("delete file {}", decodedFileName);
	}

	public void deleteAllS3Files(Set<ReviewImage> reviewImages) {
		reviewImages.forEach(reviewImage -> deleteS3File(reviewImage.getFileName()));
	}
}
