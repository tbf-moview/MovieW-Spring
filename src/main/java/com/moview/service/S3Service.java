package com.moview.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.moview.model.vo.ImageVO;
import com.moview.util.FileManager;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3Service {

	private static final String DELETE_DIR_NAME = "delete/";
	private static final String BLANK_FILE_NAME = "";

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

		} finally {
			FileManager.deleteFile(uploadFile);
		}

		String url = amazonS3.getUrl(bucket, uploadFilename).toString();
		return new ImageVO(uploadFilename, url);
	}

	private void uploadToS3(File file, String fileName) {
		amazonS3.putObject(new PutObjectRequest(bucket, fileName, file)
			.withCannedAcl(CannedAccessControlList.PublicRead));
	}

	public List<ImageVO> uploadAll(Optional<List<MultipartFile>> optionalMultipartFiles, String dirName,
		String prefixName) {

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

		} catch (AmazonClientException amazonClientException) {

			for (ImageVO image : images) {
				delete(image.fileName(), dirName);
			}

			throw new AmazonClientException(amazonClientException);

		} catch (IOException ioException) {

			for (ImageVO image : images) {
				delete(image.fileName(), dirName);
			}

			throw new RuntimeException(ioException.getMessage(), ioException);
		}

		return images;
	}

	public String delete(String fileName, String originalDirName) {

		if (!amazonS3.doesObjectExist(bucket, fileName)) {
			return BLANK_FILE_NAME;
		}

		String deleteFileName = DELETE_DIR_NAME + fileName.substring(originalDirName.length());

		amazonS3.copyObject(bucket, fileName, bucket, deleteFileName);
		amazonS3.deleteObject(bucket, fileName);
		log.info("soft delete file {} to {}", fileName, deleteFileName);

		return deleteFileName;
	}

	public void rollBack(String deletedFileName, String originalDirName) {

		if (deletedFileName.isEmpty()) {
			return;
		}

		String rollBackFileName = originalDirName + deletedFileName.substring(DELETE_DIR_NAME.length());

		amazonS3.copyObject(bucket, deletedFileName, bucket, rollBackFileName);
		amazonS3.deleteObject(bucket, deletedFileName);
		log.info("roll back file {} to {}", deletedFileName, rollBackFileName);

	}

}
