package com.moview.controller;

import com.moview.model.dto.response.CustomOAuth2User;
import com.moview.model.vo.UserVO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    @GetMapping
    public ResponseEntity<UserVO> getToken() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof CustomOAuth2User user) {
            String email = user.getName();
            String nickname = user.getNickname();

            UserVO userVO = new UserVO("ROLE_USER", email, nickname);

            return ResponseEntity.ok().body(userVO);
        }

        return ResponseEntity.ok().body(new UserVO(null, null, null));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> deleteToken(HttpServletResponse response) {
        Cookie logoutCookie = new Cookie("Authorization", null);
        logoutCookie.setMaxAge(0);
        // cookie.setSecure(true);
        logoutCookie.setPath("/");
        logoutCookie.setHttpOnly(true);
        response.addCookie(logoutCookie);

        return ResponseEntity.ok("logout successfully");
    }
}
