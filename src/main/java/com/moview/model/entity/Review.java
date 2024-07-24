package com.moview.model.entity;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.moview.common.ErrorMessage;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
	@GeneratedValue
	@UuidGenerator
	@Column(length = 36, columnDefinition = "VARCHAR(36)")
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "email", nullable = false)
	private Member member;

	@Column(name = "title", length = 100)
	@NotNull(message = ErrorMessage.TITLE_EMPTY)
	private String title;

	@Column(name = "content", length = 10_000)
	private String content;

	@Column(name = "create_date", nullable = false)
	private Timestamp createDate;

	@Column(name = "update_date")
	private Timestamp updateDate;

	@OneToMany(mappedBy = "review", cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})
	private Set<ReviewImage> reviewImages = new LinkedHashSet<>();

	@OneToMany(mappedBy = "review", cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE})
	private Set<ReviewTag> reviewTags = new LinkedHashSet<>();

	private Review(UUID id, Member member, String title, String content, Timestamp createDate, Timestamp updateDate) {
		this.id = id;
		this.member = member;
		this.title = title;
		this.content = content;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}

	public static Review of(UUID id, Member member, String title) {
		Timestamp now = Timestamp.from(Instant.now());
		return new Review(id, member, title, DEFAULT_CONTENT, now, now);
	}

	public void updateContent(String content) {

		validateEmptyContent(content);

		this.content = content;
		this.updateDate = Timestamp.from(Instant.now());
	}

	private void validateEmptyContent(String content) {

		if (Objects.isNull(content) || content.isEmpty()) {
			throw new IllegalStateException(ErrorMessage.CONTENT_EMPTY);
		}
	}

	public void addReviewImages(List<ReviewImage> reviewImages) {
		this.reviewImages.addAll(reviewImages);
	}

	public void addReviewTags(Set<ReviewTag> reviewTags) {
		this.reviewTags.addAll(reviewTags);
	}

}
