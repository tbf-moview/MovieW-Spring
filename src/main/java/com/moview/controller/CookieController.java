package com.moview.controller;

import static com.moview.util.CookieUtil.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.moview.util.JwtTokenUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CookieController {

	private final JwtTokenUtil jwtTokenUtil;

	@GetMapping("/api/token/test")
	public String readCookie(HttpServletRequest request) {
		String cookieValue = getCookieValue(request, "jwtToken");
		System.out.println(jwtTokenUtil.decodeJwt(cookieValue));
		return "/index.html";
	}

	@GetMapping("/api/token/auth")
	public String checkCookie(HttpServletRequest request) {
		String cookieValue = getCookieValue(request, "jwtToken");
		if (cookieValue == null || !jwtTokenUtil.validateToken(cookieValue)) {
			return "not authorized";
		}

		return "authorized";
	}
}