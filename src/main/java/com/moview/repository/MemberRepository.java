package com.moview.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.moview.model.entity.Member;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

	private final EntityManager em;

	public Optional<Member> findByEmail(String accountEmail) {
		return Optional.ofNullable(em.find(Member.class, accountEmail));
	}

}
