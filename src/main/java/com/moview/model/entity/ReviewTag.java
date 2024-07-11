package com.moview.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tag")
@NoArgsConstructor
public class ReviewTag {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
	@JoinColumn(name = "review_id")
	private Review review;

	@Column(length = 50)
	private String tag;

	private ReviewTag(Review review, String tag) {
		this.review = review;
		this.tag = tag;
	}

	public ReviewTag of(Review review, String tag) {
		return new ReviewTag(review, tag);
	}
}
