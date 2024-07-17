package com.moview.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileManager {

	private static final String FILE_NAME_DELIMITER = "_";

	public static File convertFile(MultipartFile originalFile, String prefixName) throws IOException {
		String originalFilename = originalFile.getOriginalFilename();
		log.info("originalFilename : {}", originalFilename);

		String uploadFilename =
			prefixName + FILE_NAME_DELIMITER
				+ UUID.randomUUID() + FILE_NAME_DELIMITER
				+ Objects.requireNonNull(originalFilename).replaceAll("\\s", FILE_NAME_DELIMITER);

		File uploadFile = new File(uploadFilename);

		File parentDir = uploadFile.getParentFile();
		if (parentDir != null && !parentDir.exists()) {
			if (!parentDir.mkdirs()) {
				throw new IOException("디렉토리를 생성할 수 없습니다: " + parentDir);
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
