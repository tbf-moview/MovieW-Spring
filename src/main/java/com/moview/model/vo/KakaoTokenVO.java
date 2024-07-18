package com.moview.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class KakaoTokenVO {
	private String accessToken;
	private String tokenType;
	private String refreshToken;
	private int expiresIn;
	private String scope;
	private int refreshTokenExpiresIn;

}