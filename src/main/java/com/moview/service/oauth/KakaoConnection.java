package com.moview.service.oauth;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.moview.model.entity.Member;
import com.moview.model.vo.KakaoTokenVO;
import com.moview.model.vo.KakaoUserVO;
import com.moview.util.HttpUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoConnection {

	private final HttpUtil httpUtil;

	@Value("${kakao.client-id}")
	private String clientId;

	@Value("${kakao.redirect-uri}")
	private String redirectUri;

	@Value("${kakao.client-secret}")
	private String clientSecret;

	public KakaoTokenVO getToken(Map<String, Object> codeMap) {
		String code = (String) codeMap.get("code");

		KakaoTokenVO kakaoTokenVO = null;

		try {
			// POST 방식으로 요청 보낼 URL 설정
			URL url = new URL("https://kauth.kakao.com/oauth/token");
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");

			//출력 스트림 활성화
			conn.setDoOutput(true);

			//요청 헤더 설정
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

			//요청 바디 설정
			StringJoiner bodyParam = new StringJoiner("&");

			bodyParam.add("grant_type=" + "authorization_code");
			bodyParam.add("client_id=" + clientId);
			bodyParam.add("redirect_url=" + redirectUri);
			bodyParam.add("code=" + code);
			bodyParam.add("client_secret=" + clientSecret);
			System.out.println(bodyParam);

			// 요청보내기
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = bodyParam.toString().getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

			JsonObject jsonObject = httpUtil.KakaoHttpUtil(conn);

			// KakaoToken 생성
			kakaoTokenVO = KakaoTokenVO.builder()
				.accessToken(jsonObject.get("access_token").getAsString())
				.tokenType(jsonObject.get("token_type").getAsString())
				.refreshToken(jsonObject.get("refresh_token").getAsString())
				.expiresIn(jsonObject.get("expires_in").getAsInt())
				.scope(jsonObject.get("scope").getAsString())
				.refreshTokenExpiresIn(jsonObject.get("refresh_token_expires_in").getAsInt())
				.build();

			System.out.println(kakaoTokenVO.toString());

			return kakaoTokenVO;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return kakaoTokenVO;
	}

	public KakaoUserVO getUserInfo(KakaoTokenVO kakaoTokenVO) {
		KakaoUserVO kakaoUserVO = null;
		try {
			// POST 방식으로 요청 보낼 URL 설정
			URL url = new URL("https://kapi.kakao.com/v2/user/me");
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("POST");

			//출력 스트림 활성화
			conn.setDoOutput(true);

			//요청 헤더 설정
			conn.setRequestProperty("Authorization", "Bearer " + kakaoTokenVO.getAccessToken());
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

			JsonObject jsonObject = httpUtil.KakaoHttpUtil(conn);

			JsonObject kakaoAccount = jsonObject.getAsJsonObject("kakao_account");
			JsonObject profile = kakaoAccount.getAsJsonObject("profile");

			// KakaoUser 생성
			kakaoUserVO = KakaoUserVO.builder()
				.profileNickname(profile.get("nickname").getAsString())
				.accountEmail(kakaoAccount.get("email").getAsString())
				.build();

			System.out.println(kakaoUserVO.toString());
			return kakaoUserVO;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return kakaoUserVO;
	}

	public Member convertToMember(KakaoUserVO kakaoUserVO) {
		// KakaoUser를 Member 객체로 변환하는 로직
		return new Member(kakaoUserVO.getAccountEmail(), kakaoUserVO.getProfileNickname());
	}
}