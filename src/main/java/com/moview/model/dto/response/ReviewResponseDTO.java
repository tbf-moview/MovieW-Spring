package com.moview.model.dto.response;

import java.sql.Timestamp;
import java.util.Set;

import com.moview.model.entity.ReviewTag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {

	private long id;
	private String title;
	private String content;
	private String nickname;
	private Set<ReviewTag> reviewTags;
	private Timestamp createDate;
	private Timestamp updateDate;
	private long likeCount;
	private boolean likeSign;
}
