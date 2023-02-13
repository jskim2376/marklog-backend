package com.marklog.blog.config;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marklog.blog.config.auth.JwtAuthenticationEntryPoint;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationEntryPointTest {
	@Mock
	HttpServletResponse response;

	@Test
	public void testJwtAuthenticationEntryPoint() throws IOException, ServletException {
		JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint = new JwtAuthenticationEntryPoint();

		//when
		jwtAuthenticationEntryPoint.commence(null, response, null);

		//then
		verify(response).sendError(anyInt(), anyString());
	}
}
