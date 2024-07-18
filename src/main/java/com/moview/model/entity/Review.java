package com.moview.model.entity;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor
@ToString
public class Review {

	private static final String DEFAULT_CONTENT = "";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne
	@JoinColumn(name = "email", nullable = false)
	private Member member;

	@Column(name = "title", length = 100)
	@NotNull(message = "[ERROR]")
	private String title;

	@Column(name = "content", length = 10_000)
	private String content;

	@Column(name = "create_date", nullable = false)
	private Timestamp createDate;

	@Column(name = "update_date")
	private Timestamp updateDate;

	@OneToMany(mappedBy = "review")
	@JsonManagedReference
	private Set<ReviewImage> reviewImages = new HashSet<>();

	@OneToMany(mappedBy = "review")
	@JsonManagedReference
	private Set<ReviewTag> reviewTags = new HashSet<>();

	private Review(Member member, String title, @Nullable String content, Timestamp createDate, Timestamp updateDate) {
		this.member = member;
		this.title = title;
		this.content = content;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}

	public static Review of(Member member, String title) {
		Timestamp now = Timestamp.from(Instant.now());
		return new Review(member, title, DEFAULT_CONTENT, now, now);
	}

	public void update(String title, String content) {
		this.title = title;
		this.content = content;
		this.updateDate = Timestamp.from(Instant.now());
	}

}
