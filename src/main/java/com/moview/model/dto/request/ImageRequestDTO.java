package com.moview.model.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageRequestDTO {

	private final String reviewTitle;
	private final List<MultipartFile> imageFiles;

}
