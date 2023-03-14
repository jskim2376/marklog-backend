package com.marklog.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.dto.AccessTokenResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/v1/token")
@RestController
public class JwtController {
	private final JwtTokenProvider jwtTokenProvider;

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/check")
	public ResponseEntity<?> tokenCheck() {
		return ResponseEntity.ok(null);
	}

	@GetMapping("/refresh")
	public ResponseEntity<AccessTokenResponseDto> tokenRefresh(@CookieValue(value = "refresh_token") String refresh_token) {
		if (refresh_token != null && jwtTokenProvider.validateToken(refresh_token)) {
			Long id = jwtTokenProvider.getId(refresh_token);
			String email = jwtTokenProvider.getEmail(refresh_token);
			String accessToken = jwtTokenProvider.createAccessToken(id, email);
			AccessTokenResponseDto accessTokenResponseDto = new AccessTokenResponseDto(accessToken);
			return ResponseEntity.ok(accessTokenResponseDto);
		} else {
			return ResponseEntity.badRequest().build();
		}
	}
}
