package com.moview.model.entity;

import java.sql.Timestamp;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "preference")
@NoArgsConstructor
@Getter
@ToString
public class Preference {

	@Id
	@ManyToOne
	@JoinColumn(name = "email", nullable = false)
	private Member member;

	@Id
	@ManyToOne
	@JoinColumn(name = "review_id", nullable = false)
	private Review review;

	@Column(nullable = false)
	private Timestamp likeDate;

	@Column(columnDefinition = "TINYINT(1)")
	private boolean likeSign;

	private Preference(Member member, Review review, Timestamp likeDate, boolean likeSign) {
		this.member = member;
		this.review = review;
		this.likeDate = likeDate;
		this.likeSign = likeSign;
	}

	public static Preference of(Member member, Review review) {
		return new Preference(member, review, Timestamp.from(Instant.now()), true);
	}

	public void updateLike() {
		this.likeSign = !likeSign;
		this.likeDate = Timestamp.from(Instant.now());
	}

}
