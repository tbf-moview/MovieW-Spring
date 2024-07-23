package com.moview.model.dto.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewListResponseDTO {

	private long id;
	private String email;
	private String nickname;
	private String title;
	private String content;
	private long likeCount;
	private Timestamp createDate;
	private Timestamp updateDate;

}
