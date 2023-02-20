package com.marklog.blog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.config.auth.dto.UserAuthenticationDto;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.service.PostLikeService;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = PostLikeController.class)
@ContextConfiguration(classes = { PostLikeController.class, JwtTokenProvider.class })
public class PostLikeControllerTest {
	@Autowired
	private MockMvc mvc;
	@MockBean
	private PostLikeService postLikeService;

	@Test
	public void testSavePostLikeByPostConrtoller() throws Exception {
		// given
		String path = "/v1/post/like/1";
		UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(1L, "test@test.com", Role.USER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userAuthenticationDto, null, Collections.singleton(new SimpleGrantedAuthority(userAuthenticationDto.getRole().getKey())));
		// when
		ResultActions ra = mvc.perform(post(path)
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(SecurityMockMvcRequestPostProcessors.authentication(authentication)));

		// then
		ra.andExpect(status().isCreated());
	}


	@Test
	public void testDeletePostLikeByPostController() throws Exception {
		// given
		String path = "/v1/post/like/1";
		UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(1L, "test@test.com", Role.USER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userAuthenticationDto, null, Collections.singleton(new SimpleGrantedAuthority(userAuthenticationDto.getRole().getKey())));

		// when
		ResultActions ra = mvc.perform(delete(path)
				.with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
				.with(SecurityMockMvcRequestPostProcessors.csrf()));

		// then
		ra.andExpect(status().isNoContent());
	}
}
