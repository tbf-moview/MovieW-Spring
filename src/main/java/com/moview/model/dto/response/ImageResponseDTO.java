package com.moview.model.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ImageResponseDTO {
	private long reviewID;
	private List<String> imageURLs;
}
