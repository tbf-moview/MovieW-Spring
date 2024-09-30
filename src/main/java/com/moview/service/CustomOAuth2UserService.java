package com.moview.service;

import com.moview.model.dto.response.CustomOAuth2User;
import com.moview.model.dto.response.GoogleResponseDto;
import com.moview.model.dto.response.OAuth2Response;
import com.moview.model.entity.Member;
import com.moview.model.vo.UserVO;
import com.moview.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response;
        if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponseDto(oAuth2User.getAttributes());
        } else {

            return null;
        }

        UserVO userVO = new UserVO(
                "ROLE_USER",
                oAuth2Response.getEmail(),
                oAuth2Response.getName()
        );

        Optional<Member> member = memberRepository.findByEmail(userVO.email());

        if (member.isEmpty()) {
            memberRepository.save(new Member(
                    userVO.email(),
                    userVO.username()
            ));
        }

        return new CustomOAuth2User(userVO);

    }

}
