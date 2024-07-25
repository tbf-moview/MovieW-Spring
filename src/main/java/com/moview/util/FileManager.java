package com.moview.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import org.springframework.web.multipart.MultipartFile;

import com.moview.common.ErrorMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileManager {

	private static final String FILE_NAME_DELIMITER = "_";
	private static final String SPACE = "\\s";

	public static File convertFile(MultipartFile originalFile, String prefixName) throws IOException {
		String originalFilename = originalFile.getOriginalFilename();
		log.info("originalFilename : {}", originalFilename);

		String uploadFilename =
			prefixName + FILE_NAME_DELIMITER
				+ Objects.requireNonNull(originalFilename).replaceAll(SPACE, FILE_NAME_DELIMITER);

		File uploadFile = new File(uploadFilename);

		File parentDir = uploadFile.getParentFile();
		if (parentDir != null && !parentDir.exists()) {
			if (!parentDir.mkdirs()) {
				throw new IOException(ErrorMessage.DIRECTOR_CANT_CREATE);
			}
		}

		FileOutputStream fileOutputStream = new FileOutputStream(uploadFile);
		fileOutputStream.write(originalFile.getBytes());
		fileOutputStream.close();
		log.info("uploadFilename  before return : {}", uploadFilename);

		return uploadFile;
	}

	public static void deleteFile(File file) {

		if (file.delete()) {
			log.info("{} 삭제완료", file.getName());
		} else {
			log.info("{} 삭제실패", file.getName());
		}
	}

}
