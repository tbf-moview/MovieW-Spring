package com.moview.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

import com.moview.common.JwtTokenProvider;
import com.moview.model.entity.Member;
import com.moview.model.vo.KakaoToken;
import com.moview.model.vo.KakaoUser;
import com.moview.model.vo.MoviewToken;
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
	public String callback(@RequestBody Map<String, Object> codeMap, HttpServletResponse response) {

		KakaoToken kakaoTokenDTO = kakaoConnection.getToken(codeMap);
		KakaoUser kakaoUserDTO = kakaoConnection.getUserInfo(kakaoTokenDTO);

		Member member = kakaoConnection.convertToMember(kakaoUserDTO);

		//토큰 생성
		MoviewToken moviewTokenDTO = new MoviewToken(kakaoUserDTO.getAccountEmail(),
			kakaoUserDTO.getProfileNickname());
		String moviewAccessToken = jwtTokenProvider.generateAccessToken(moviewTokenDTO.getEmail(),
			moviewTokenDTO.getNickname());
		String moviewRefreshToken = jwtTokenProvider.generateRefreshToken(moviewTokenDTO.getEmail());

		// 쿠키 생성
		CookieUtil.createCookie(response,"jwtToken",moviewAccessToken,43200);

		return "http://localhost:5173/";
	}

}
