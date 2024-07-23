package com.moview.controller;

import static com.moview.util.CookieUtil.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.moview.util.JwtTokenUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CookieController {
	@Autowired
	JwtTokenUtil jwtTokenUtil;

	@GetMapping("/api/token/test")
	public String readCookie(HttpServletRequest request) {
		String cookieValue = getCookieValue(request, "jwtToken");
		System.out.println(jwtTokenUtil.decodeJwt(cookieValue));
		return "/index.html";
	}
}