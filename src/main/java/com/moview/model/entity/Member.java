package com.moview.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "member")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Member {

	@Id
	@Column(length = 100)
	private String email;

	@Column(length = 100)
	private String nickname;

	public Member(String email){
		this.email = email;
	}
}
