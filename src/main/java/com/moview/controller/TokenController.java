package com.moview.controller;

import com.moview.model.dto.response.CustomOAuth2User;
import com.moview.model.vo.UserVO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    @GetMapping
    public ResponseEntity<UserVO> getToken() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof CustomOAuth2User) {
            CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
            String email = user.getName();
            String nickname = user.getNickname();

            UserVO userVO = new UserVO("ROLE_USER", email, nickname);

            return ResponseEntity.ok().body(userVO);
        }

        return ResponseEntity.ok().body(new UserVO(null, null, null));
    }
}
