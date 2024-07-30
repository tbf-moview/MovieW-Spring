package com.moview.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.moview.common.ErrorMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileManager {

	private static final String FILE_NAME_DELIMITER = "_";
	private static final String SPACE = "\\s";

	private static final String START_IMAGE_TAG = "<img src=\"";
	private static final String END_IMAGE_TAG = "\">";
	private static final String HOST_NAME = "https://tbf-moview-test.s3.ap-northeast-2.amazonaws.com/";

	public static File convertFile(MultipartFile originalFile, String prefixName) throws IOException {
		String originalFilename = originalFile.getOriginalFilename();
		log.info("originalFilename : {}", originalFilename);

		String uploadFilename =
			prefixName + FILE_NAME_DELIMITER
				+ UUID.randomUUID() + FILE_NAME_DELIMITER
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

	public static List<String> extractImageFileNameInContent(List<String> texts) {

		String text = combineText(texts);
		List<String> fileNames = new ArrayList<>();

		while (text.contains(START_IMAGE_TAG)) {

			text = text.substring(text.indexOf(START_IMAGE_TAG) + START_IMAGE_TAG.length());

			String fileName = text.substring(HOST_NAME.length(), text.indexOf(END_IMAGE_TAG));
			fileNames.add(fileName);
		}

		System.out.println(fileNames);

		return fileNames;
	}

	private static String combineText(List<String> texts) {

		StringBuilder stringBuilder = new StringBuilder();

		for (String text : texts) {
			stringBuilder.append(text);
		}

		return stringBuilder.toString();
	}

}
