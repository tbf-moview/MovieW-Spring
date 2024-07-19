package com.moview.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.moview.common.ErrorMessage;
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
		return memberRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException(ErrorMessage.MEMBER_NOT_EXIST));
	}

	public boolean saveMember(Member member) {
		Optional<Member> existingMember = memberRepository.findByEmail(member.getEmail());
		if (existingMember.isPresent()) {
			return false; // 이미 존재하는 경우 저장하지 않음
		}
		memberRepository.save(member);
		return true; // 저장 성공
	}

	public void deleteMember(String email) {
		memberRepository.deleteMemberByEmail(email);
	}

	public void deleteAll(){
		memberRepository.deleteAll();
	}

}
