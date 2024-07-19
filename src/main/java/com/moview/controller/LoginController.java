package com.moview.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

import com.moview.common.JwtTokenProvider;
import com.moview.model.entity.Member;
import com.moview.model.vo.KakaoTokenVO;
import com.moview.model.vo.KakaoUserVO;
import com.moview.model.vo.MoviewTokenVO;
import com.moview.service.MemberService;
import com.moview.service.oauth.KakaoConnection;
import com.moview.util.CookieUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/login")
public class LoginController {

	private final MemberService memberService;
	private final KakaoConnection kakaoConnection;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping("/kakao")
	public ResponseEntity<String> callback(@RequestBody Map<String, Object> codeMap, HttpServletResponse response) {

		KakaoTokenVO kakaoTokenVO = kakaoConnection.getToken(codeMap);
		KakaoUserVO kakaoUserVO = kakaoConnection.getUserInfo(kakaoTokenVO);

		Member member = kakaoConnection.convertToMember(kakaoUserVO);
		memberService.saveMember(member);

		//토큰 생성
		MoviewTokenVO moviewTokenVO = new MoviewTokenVO(kakaoUserVO.getEmail(),
			kakaoUserVO.getNickname());
		String moviewAccessToken = jwtTokenProvider.generateAccessToken(moviewTokenVO.getEmail(),
			moviewTokenVO.getNickname());
		String moviewRefreshToken = jwtTokenProvider.generateRefreshToken(moviewTokenVO.getEmail());

		// 쿠키 생성
		CookieUtil.createCookie(response,"jwtToken",moviewAccessToken,43200);
		System.out.println(jwtTokenProvider.decodeJwt(moviewAccessToken));

		return ResponseEntity.ok("login successfully");
	}

}
