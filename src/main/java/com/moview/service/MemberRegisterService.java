package com.moview.service;

import org.springframework.context.ApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moview.model.entity.Member;
import com.moview.repository.MemberRepository;
import com.moview.util.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberRegisterService {

	private final ApplicationContext applicationContext;
	private final JwtTokenUtil jwtTokenUtil;
	private final MemberRepository memberRepository;
	private final JavaMailSender emailSender;

	@Transactional
	public boolean registerMember(String email,String nickname) {
		Member member = new Member(email);
		String jwtToken = jwtTokenUtil.generateRegistrationToken(email,nickname);

		sendVerificationEmail(member,jwtToken);
		return verifyMember(jwtToken);

	}

	private void sendVerificationEmail(Member member,String token) {
		String subject = "Please verify your email";
		String verificationUrl = "http://localhost:8080/verify?token=" + token;

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(member.getEmail());
		mailMessage.setSubject(subject);
		mailMessage.setText("다음 링크를 클릭하시면 회원가입이 완료됩니다. " + verificationUrl);

		emailSender.send(mailMessage);
	}

	@Transactional
	public boolean verifyMember(String token) {
		if (jwtTokenUtil.validateToken(token)) {
			String email = jwtTokenUtil.extractUserEmail(token);
			Member member = memberRepository.findByEmail(email).get();
			if (member != null) {
				memberRepository.save(member);
				return true;
			}
		}
		return false;
	}

	private MemberRegisterService getSelf() {
		return applicationContext.getBean(MemberRegisterService.class);
	}
}
