package com.moview.model.entity;

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
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "images")
@ToString
public class ReviewImage {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
	@JoinColumn(name = "review_id", nullable = false)
	private Review review;

	@Column(nullable = false, length = 1000)
	private String fileName;

	@Column(length = 1000)
	private String fileUrl;

	private ReviewImage(Review review, String fileName, String fileUrl) {
		this.review = review;
		this.fileName = fileName;
		this.fileUrl = fileUrl;
	}

	public static ReviewImage of(Review review, String fileName, String fileURL) {
		return new ReviewImage(review, fileName, fileURL);
	}

}
