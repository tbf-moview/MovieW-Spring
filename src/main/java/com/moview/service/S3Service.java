package com.moview.service;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.moview.model.vo.ImageVO;
import com.moview.util.FileManager;

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

	public ImageVO upload(MultipartFile file, String dirName, String prefixName) throws IOException {

		File uploadFile = FileManager.convertFile(file, prefixName);
		String uploadFilename = dirName + uploadFile.getName();
		log.info("Uploaded file name : {}", uploadFilename);

		try {
			uploadToS3(uploadFile, uploadFilename);

		} catch (Exception e) {
			log.error("파일 업로드 중 예외 발생 : {} - {}", e.getClass().getSimpleName() , e.getMessage(), e);
			FileManager.deleteFile(uploadFile);
			throw new RuntimeException(e);
		}

		String url = amazonS3.getUrl(bucket, uploadFilename).toString();
		return new ImageVO(uploadFilename, url);
	}

	public List<ImageVO> uploadAll(Optional<List<MultipartFile>> optionalMultipartFiles, String dirName, String prefixName) {

		if (optionalMultipartFiles.isEmpty()) {
			return new ArrayList<>();
		}

		List<MultipartFile> originalFiles = optionalMultipartFiles.get();
		List<ImageVO> images = new ArrayList<>();

		try {

			for (MultipartFile file : originalFiles) {
				ImageVO image = upload(file, dirName, prefixName);
				images.add(image);
			}

		}catch (IOException | RuntimeException e) {
			log.error(e.getMessage());

			for (ImageVO image : images) {
				deleteS3File(image.fileName());
			}

			throw new RuntimeException(e);
		}

		return images;
	}

	private void uploadToS3(File file, String fileName) {

		amazonS3.putObject(new PutObjectRequest(bucket, fileName, file)
			.withCannedAcl(CannedAccessControlList.PublicRead));
	}

	public void deleteS3File(String fileName) {

		String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
		amazonS3.deleteObject(bucket, decodedFileName);
		log.info("delete file {}", decodedFileName);
	}

}
