package com.moview.model.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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

	@NotNull(message = "[ERROR] 제목은 null이 될 수 없습니다.")
	@NotEmpty(message = "[ERROR] 제목을 입력해주세요")
	private String title;

	private List<MultipartFile> images;

	@NotNull
	@Size(min = 1, message = "[ERROR] 내용을 입력해주세요")
	private List<String> texts;

	private List<String> tags;

}
