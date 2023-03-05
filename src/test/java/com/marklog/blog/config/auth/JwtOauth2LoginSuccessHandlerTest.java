package com.marklog.blog.config.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

@ExtendWith(MockitoExtension.class)
public class JwtOauth2LoginSuccessHandlerTest {
	@Test
	public void testJwtOauth2LoginSuccessHandler() throws IOException, ServletException {
		//given
		long id = 1L;
		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(800L, 800L);
		JwtOAuth2LoginSuccessHandler jwtOAuth2LoginSuccessHandler = new JwtOAuth2LoginSuccessHandler(jwtTokenProvider);

		HttpServletResponse response = new MockHttpServletResponse();

		Authentication authentication = mock(Authentication.class);
		OAuth2User oAuth2User = mock(OAuth2User.class);
		when(authentication.getPrincipal()).thenReturn(oAuth2User);
		when(oAuth2User.getAttribute("email")).thenReturn("email");
		when(oAuth2User.getAttribute("id")).thenReturn(id);
		
		
		//when
		jwtOAuth2LoginSuccessHandler.onAuthenticationSuccess(null, response, authentication);
		
		//then
		System.out.print(response.getHeader("Set-Cookie"));
		assertThat(response.getHeader("Set-Cookie").contains("refresh_token")).isTrue();
		assertThat(response.getHeader("Set-Cookie").contains("HttpOnly")).isTrue();
		assertThat(response.getHeader("Set-Cookie").contains("Secure")).isTrue();
		
		assertThat(response.getContentType()).isEqualTo("application/json");
		assertThat(response.getHeader("Location")).isEqualTo("/");
		assertThat(response.getStatus()).isEqualTo(302);
		
	}

}
