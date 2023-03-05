package com.marklog.blog.controller;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

	Long postId = 1L;
	Long userId = 1L;
	String path = "/v1/post/1/like";
	private Authentication authentication;

	@BeforeEach
	public void setup() {
		UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(userId, "test@test.com", Role.USER);
		authentication = new UsernamePasswordAuthenticationToken(userAuthenticationDto, null,
				Collections.singleton(new SimpleGrantedAuthority(userAuthenticationDto.getRole().getKey())));

	}

	@Test
	public void testSavePostLikeByPostConrtoller() throws Exception {
		// given
		// when
		ResultActions ra = mvc.perform(post(path).with(csrf()).with(authentication(authentication)));

		// then
		ra.andExpect(status().isCreated());
	}

	@Test
	public void testSavePostLikeByPostConrtoller_not_found() throws Exception {
		// given
		doThrow(JpaObjectRetrievalFailureException.class).when(postLikeService).save(postId, userId);
		// when
		ResultActions ra = mvc.perform(post(path).with(csrf()).with(authentication(authentication)));

		// then
		ra.andExpect(status().isNotFound());
	}

	@Test
	public void testDeletePostLikeByPostController() throws Exception {
		// given
		// when
		ResultActions ra = mvc.perform(delete(path).with(authentication(authentication)).with(csrf()));

		// then
		ra.andExpect(status().isNoContent());
	}

	@Test
	public void testDeletePostLikeByPostController_not_found() throws Exception {
		// given
		doThrow(NoSuchElementException.class).when(postLikeService).delete(postId, userId);
		// when
		ResultActions ra = mvc.perform(delete(path).with(authentication(authentication)).with(csrf()));

		// then
		ra.andExpect(status().isNotFound());
	}
}
