package com.moview.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.moview.model.entity.Member;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

	@PersistenceContext
	private final EntityManager em;

	public Optional<Member> findByEmail(String email) {
		return Optional.ofNullable(em.find(Member.class, email));
	}


	public void save(Member member) {
		em.persist(member);
	}


	public void deleteMemberByEmail(String email) {
		Member member = em.find(Member.class, email);
		if (member != null) {
			em.remove(member);
		} else {
			throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
		}
	}

	public void deleteAll(){
		
	}

}
