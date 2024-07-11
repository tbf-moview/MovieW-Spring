package com.moview.service;

import org.springframework.stereotype.Service;

import com.moview.model.entity.Member;
import com.moview.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

	private final MemberRepository memberRepository;

	public Member findByEmail(String email){
		return memberRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("[ERROR] 존재하지 않는 Email"));
	}

}
