package com.marklog.blog.config.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.marklog.blog.service.UserService;

@ExtendWith(MockitoExtension.class)
public class JwtAuthentificationFilterTest {
	@Mock
	JwtTokenProvider jwtTokenProvider;
	@Mock
	UserService userService;
	@Mock
	HttpServletRequest request;
	@Mock
	HttpServletResponse response;
	@Mock
	FilterChain filterChain;

	@Test
	public void testJwtAuthentificationFilter() throws ServletException, IOException {
		// given
		String token = "testToken";
		when(jwtTokenProvider.parseBearerToken(request)).thenReturn(token);
		when(jwtTokenProvider.validateToken(token)).thenReturn(true);
		Authentication authentication = new UsernamePasswordAuthenticationToken(null, null, null);
		when(jwtTokenProvider.getAuthentication(anyString(), any(UserService.class))).thenReturn(authentication);
		// when
		JwtAuthentificationFilter jwtAuthentificationFilter = new JwtAuthentificationFilter(jwtTokenProvider,
				userService);
		jwtAuthentificationFilter.doFilter(request, response, filterChain);

		// then
		verify(filterChain).doFilter(request, response);
	}

}
