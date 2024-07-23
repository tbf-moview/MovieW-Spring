package com.moview.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

import com.moview.util.JwtTokenUtil;
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
@RequestMapping("/api/login")
public class LoginController {

	private final MemberService memberService;
	private final KakaoConnection kakaoConnection;
	private final JwtTokenUtil jwtTokenUtil;

	@PostMapping("/kakao")
	public ResponseEntity<String> callback(@RequestBody Map<String, Object> codeMap, HttpServletResponse response) {

		KakaoTokenVO kakaoTokenVO = kakaoConnection.getToken(codeMap);
		KakaoUserVO kakaoUserVO = kakaoConnection.getUserInfo(kakaoTokenVO);

		Member member = kakaoConnection.convertToMember(kakaoUserVO);
		memberService.saveMember(member);

		//토큰 생성
		MoviewTokenVO moviewTokenVO = new MoviewTokenVO(kakaoUserVO.getEmail(),
			kakaoUserVO.getNickname());
		String moviewAccessToken = jwtTokenUtil.generateAccessToken(moviewTokenVO.getEmail(),
			moviewTokenVO.getNickname());
		String moviewRefreshToken = jwtTokenUtil.generateRefreshToken(moviewTokenVO.getEmail());

		// 쿠키 생성
		CookieUtil.createCookie(response,"jwtToken",moviewAccessToken,43200);
		return ResponseEntity.ok("login successfully");
	}

}
