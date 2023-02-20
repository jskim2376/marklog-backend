package com.marklog.blog.config.auth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
public class JwtOauthLoginSuccessHandlerTest {
	@Mock
	OAuth2User oauth2User;
	@Mock
	HttpServletRequest request;
	@Mock
	HttpServletResponse response;
	@Mock
	Authentication authentication;

	@Mock
	JwtTokenProvider jwtTokenProvider;

	@Mock
	PrintWriter printWriter;

	Long id;
	String email;
	String token;

	@BeforeEach
	public void setUp() {
		id = 1L;
		email = "test@test.com";
		token = "test_token";
	}

	@Test
	public void testJwtOauthLoginSuccessHandler() throws IOException, ServletException {
		// given
		JwtOAuth2LoginSuccessHandler jwtOauthLoginSuccessHandlerTest = new JwtOAuth2LoginSuccessHandler(
				jwtTokenProvider);
		when(authentication.getPrincipal()).thenReturn(oauth2User);
		when(oauth2User.getAttribute("email")).thenReturn(email);
		when(oauth2User.getAttribute("id")).thenReturn(id);
		when(jwtTokenProvider.createAccessToken(id, email)).thenReturn(token);
		when(response.getWriter()).thenReturn(printWriter);
		when(jwtTokenProvider.getRefreshtoken_expired()).thenReturn(100000L);
		// when
		jwtOauthLoginSuccessHandlerTest.onAuthenticationSuccess(request, response, authentication);

		// then
		verify(printWriter).write(anyString());
		verify(response).setContentType(anyString());
		verify(response).addHeader(eq("Set-Cookie"), anyString());
	}

}
