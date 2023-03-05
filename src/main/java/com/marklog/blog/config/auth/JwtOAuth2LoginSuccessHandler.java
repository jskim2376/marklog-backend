package com.marklog.blog.config.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklog.blog.controller.dto.AccessTokenDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
		private final JwtTokenProvider jwt;

	    @Override
		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
	            throws IOException, ServletException {
	        OAuth2User oauth2User = (OAuth2User)authentication.getPrincipal();
	        String email = oauth2User.getAttribute("email");
	        Long id = oauth2User.getAttribute("id");


	        String refresh_token = jwt.createRefreshToken(id,email);
	        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refresh_token)
	        		.path("/api")
	        		.sameSite("strict")
	        		.httpOnly(true)
	        		.secure(true)
	        		.maxAge((int) (jwt.getRefreshtoken_expired() / 1000))
	        		.build();
	        response.addHeader("Set-Cookie", responseCookie.toString());

	        String access_token = jwt.createAccessToken(id,email);
	        AccessTokenDto accessTokenDto = new AccessTokenDto(access_token);
	        ObjectMapper mapper = new ObjectMapper();
	        response.getWriter().write(mapper.writeValueAsString(accessTokenDto));
	        response.setContentType("application/json");
	        response.setHeader("Location", "/");
	        response.setStatus(302);
	}
}
