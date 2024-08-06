package com.moview.model.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReviewSearchRequestDTO {

	String searchOption;
	String searchWord;
	String sortOption;
	int page;

}
