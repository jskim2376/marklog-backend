package com.marklog.blog.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.marklog.blog.config.auth.JwtTokenProvider;

@WebMvcTest(controllers = JwtController.class)
@ContextConfiguration(classes = { JwtController.class, JwtTokenProvider.class })
public class JwtControllerTest {
	@Autowired
	private MockMvc mvc;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@WithMockUser(roles = "USER")
	@Test
	public void testCheckJwtConrtoller() throws Exception {
		// given
		String path = "/v1/token/check";

		// when
		ResultActions ra = mvc.perform(get(path).with(csrf()));

		// then
		ra.andExpect(status().isOk());
	}

	@WithMockUser(roles = "USER")
	@Test
	public void testRefreshJwtController() throws Exception {
		String path = "/v1/token/refresh";
		String refreshToken = jwtTokenProvider.createRefreshToken(1L, "test@gmail.com");
		Cookie cookie = new Cookie("refresh_token", refreshToken);

		// when
		ResultActions ra = mvc.perform(get(path).with(csrf()).cookie(cookie));

		ResultActions raBad = mvc.perform(get(path).with(csrf()));

		// then
		ra.andExpect(status().isOk());
		raBad.andExpect(status().isBadRequest());

	}

}
