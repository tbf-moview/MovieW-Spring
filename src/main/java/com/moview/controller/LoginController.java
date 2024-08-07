package com.moview.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moview.model.entity.Member;
import com.moview.model.vo.KakaoTokenVO;
import com.moview.model.vo.KakaoUserVO;
import com.moview.model.vo.MoviewTokenVO;
// import com.moview.service.MemberRegisterService;
import com.moview.service.MemberRegisterService;
import com.moview.service.MemberService;
import com.moview.service.oauth.KakaoConnection;
import com.moview.util.CookieUtil;
import com.moview.util.JwtTokenUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

	private final MemberService memberService;
	private final KakaoConnection kakaoConnection;
	private final JwtTokenUtil jwtTokenUtil;
	private final MemberRegisterService memberRegisterService;

	@PostMapping("/login/kakao")
	public ResponseEntity<String> kakaoCallback(@RequestBody Map<String, Object> codeMap,
		HttpServletResponse response) {

		KakaoTokenVO kakaoTokenVO = kakaoConnection.getToken(codeMap);
		KakaoUserVO kakaoUserVO = kakaoConnection.getUserInfo(kakaoTokenVO);

		Member member = kakaoConnection.convertToMember(kakaoUserVO);
		memberService.saveMember(member);

		//토큰 생성
		MoviewTokenVO moviewTokenVO = new MoviewTokenVO(kakaoUserVO.getEmail(), kakaoUserVO.getNickname());
		String moviewAccessToken = jwtTokenUtil.generateAccessToken(moviewTokenVO.getEmail(),
			moviewTokenVO.getNickname());
		String moviewRefreshToken = jwtTokenUtil.generateRefreshToken(moviewTokenVO.getEmail());

		// 쿠키 생성
		CookieUtil.createCookie(response, "jwtToken", moviewAccessToken, 43200);
		CookieUtil.createCookie(response, "jwtToken", moviewRefreshToken, 43200);
		return ResponseEntity.ok("login successfully");
	}

	// @GetMapping("/login/google")
	// public String googleCallback() {
	// 	return "redirect:/http://localhost:8080/oauth2callback";
	// }

	// @GetMapping("/login/naver")
	// public String naverCallback() {
	// 	return "as";
	// }

	// @PostMapping("/login/moview")
	// 로그인

	@PostMapping("/signup/moview")
	public ResponseEntity<?> signin(@RequestBody Map<String, String> emailMap){
		String email = emailMap.get("email");

		String nickname = memberRegisterService.substringEmail(email);
		Member member = new Member(email,nickname);
		String jwtToken = jwtTokenUtil.generateAccessToken(email,nickname);
		memberRegisterService.sendVerificationEmail(member,jwtToken);

		return ResponseEntity.ok().body("success");
	}

	// 이메일 전송

	@GetMapping("/login/verify")
	// 이메일에 있는 링크 클릭
	public String verifyMember(@RequestParam("token") String token) {
		String email = jwtTokenUtil.extractUserEmail(token);
		String nickname = jwtTokenUtil.extractNickname(token);
		if (memberRegisterService.registerMember(email)) {
			return "Email verified successfully";
		} else {
			return "Invalid token or token expired";
		}
	}


	// 엑세스 토큰 검증 (헤더에 있는 토큰을 검증 -> success, fail)
	// @GetMapping


}
