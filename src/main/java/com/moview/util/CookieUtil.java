package com.moview.util;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

	// 쿠키를 생성하고 설정하는 메서드
	public static void createCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAgeInSeconds); // 유효 시간 설정 (초 단위)
		cookie.setPath("/"); // 쿠키의 유효 경로 설정 (루트 경로로 설정)

		// HTTPS를 사용할 경우 쿠키도 Secure 속성을 설정해야 함
		// cookie.setSecure(true);

		// HttpOnly 쿠키 설정 (JavaScript에서 접근 불가)
		cookie.setHttpOnly(true);

		response.addCookie(cookie);
	}

	// 쿠키 값을 가져오는 메서드
	public static String getCookieValue(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	// 쿠키를 삭제하는 메서드
	public static void deleteCookie(HttpServletResponse response, String name) {
		Cookie cookie = new Cookie(name, "");
		cookie.setMaxAge(0); // 쿠키의 유효 시간을 0으로 설정하여 삭제
		cookie.setPath("/");
		response.addCookie(cookie);
	}
}