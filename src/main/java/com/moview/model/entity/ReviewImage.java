package com.moview.model.entity;

import java.util.Objects;

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

@Getter
@Entity
@NoArgsConstructor
@Table(name = "images")
public class ReviewImage {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
	@JoinColumn(name = "review_id", nullable = false)
	@JsonBackReference
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

	@Override
	public String toString() {
		return "ReviewImage (id=" + id + ", fileName=" + fileName + ", fileUrl=" + fileUrl + ")";
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ReviewImage that = (ReviewImage)o;
		return Objects.equals(review, that.review) && Objects.equals(fileName, that.fileName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(review, fileName);
	}

}
