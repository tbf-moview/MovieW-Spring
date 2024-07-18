package com.moview.model.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.moview.common.ErrorMessage;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewRequestDTO {

	private static final int TEXTS_MIN_SIZE = 1;

	@NotNull(message = ErrorMessage.TITLE_EMPTY)
	@NotEmpty(message = ErrorMessage.TITLE_EMPTY)
	private String title;

	private List<MultipartFile> images;

	@NotNull
	@Size(min = TEXTS_MIN_SIZE, message = ErrorMessage.CONTENT_EMPTY)
	private List<String> texts;

	private List<String> tags;

}
