package com.marklog.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.controller.dto.AccessTokenDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/v1/token")
@RestController
public class JwtController {
	private	final JwtTokenProvider jwtTokenProvider;

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/check")
	public ResponseEntity<?> tokenCheck() {
        return  ResponseEntity.ok(null);
	}

	@GetMapping("/refresh")
	public ResponseEntity<AccessTokenDto> tokenRefresh(@CookieValue(value="refresh_token") String refresh_token) {
		if(refresh_token != null && jwtTokenProvider.validateToken(refresh_token)) {
			Long id = jwtTokenProvider.getId(refresh_token);
			String email = jwtTokenProvider.getEmail(refresh_token);
			String accessToken = jwtTokenProvider.createAccessToken(id, email);
			AccessTokenDto accessTokenDto = new AccessTokenDto(accessToken);
			return ResponseEntity.ok(accessTokenDto);
		}
		else {
	        return  ResponseEntity.badRequest().body(null);
		}
	}
}
