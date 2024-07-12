package com.moview.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileConverter {

	private static final String s3ReviewImagePath = "review-images/";
	private static final String fileNameDelimiter = "_";

	public static File convertFile(MultipartFile originalFile, String prefixName) throws IOException {
		String originalFilename = originalFile.getOriginalFilename();
		log.info("originalFilename : {}", originalFilename);

		String uploadedFilename =
			s3ReviewImagePath
				+ prefixName + fileNameDelimiter
				+ UUID.randomUUID() + fileNameDelimiter
				+ Objects.requireNonNull(originalFilename).replaceAll("\\s", fileNameDelimiter);

		File uploadFile = new File(uploadedFilename);

		File parentDir = uploadFile.getParentFile();
		if (parentDir != null && !parentDir.exists()) {
			if (!parentDir.mkdirs()) {
				throw new IOException("디렉토리를 생성할 수 없습니다: " + parentDir);
			}
		}

		FileOutputStream fileOutputStream = new FileOutputStream(uploadFile);
		fileOutputStream.write(originalFile.getBytes());
		fileOutputStream.close();

		return uploadFile;
	}

}
