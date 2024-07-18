package com.moview.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tag")
@NoArgsConstructor
public class ReviewTag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne
	@JoinColumn(name = "review_id")
	@JsonBackReference
	private Review review;

	@Column(length = 50)
	private String tag;

	private ReviewTag(Review review, String tag) {
		this.review = review;
		this.tag = tag;
	}

	public static ReviewTag of(Review review, String tag) {
		return new ReviewTag(review, tag);
	}

	@Override
	public String toString() {
		return "ReviewTag (id=" + id + ", tag=" + tag + ")";
	}
}
