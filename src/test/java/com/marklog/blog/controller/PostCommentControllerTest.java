package com.marklog.blog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.config.auth.dto.UserAuthenticationDto;
import com.marklog.blog.controller.dto.PostCommentRequestDto;
import com.marklog.blog.controller.dto.PostCommentResponseDto;
import com.marklog.blog.controller.dto.PostCommentUpdateRequestDto;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.service.PostCommentService;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = PostCommentController.class)
@ContextConfiguration(classes = { PostCommentController.class, JwtTokenProvider.class })
public class PostCommentControllerTest {
	@Autowired
	private MockMvc mvc;

	@MockBean
	private static PostCommentService postCommentService;
	private static Authentication authentication;
	Long postId = 1L;
	Long id = 1L;
	String commentContent = "comment";

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@BeforeAll
	public static void setUp() {
		UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(1L, "test@test.com", Role.USER);
		authentication = new UsernamePasswordAuthenticationToken(userAuthenticationDto, null,
				Collections.singleton(new SimpleGrantedAuthority(userAuthenticationDto.getRole().getKey())));
	}

	@WithMockUser
	@Test
	public void testGetAllByPostCommentControllerUserConrtoller() throws Exception {
		// given
		String path = "/v1/post/1/comment";
		PostCommentResponseDto postCommentResponseDtoSub = new PostCommentResponseDto(id, "name",
				commentContent + "sub", null);
		List<PostCommentResponseDto> child = new ArrayList<>();
		child.add(postCommentResponseDtoSub);

		PostCommentResponseDto postCommentResponseDto = new PostCommentResponseDto(id, "name", commentContent, child);
		PostCommentResponseDto postCommentResponseDto2 = new PostCommentResponseDto(id, "name", commentContent + 2,
				null);

		List<PostCommentResponseDto> commentResponseDtos = new ArrayList<>();
		commentResponseDtos.add(postCommentResponseDto);
		commentResponseDtos.add(postCommentResponseDto2);

		when(postCommentService.findAll(postId)).thenReturn(commentResponseDtos);

		// when
		ResultActions ra = mvc.perform(get(path).with(csrf()));
		// then
		ra.andExpect(status().isOk()).andExpect(jsonPath("$[0].content").value(commentContent));
		ra.andExpect(status().isOk()).andExpect(jsonPath("$[0].childList[0].content").value(commentContent + "sub"));
		ra.andExpect(status().isOk()).andExpect(jsonPath("$[1].content").value(commentContent + 2));

	}

	@Test
	public void testPostPostCommentController() throws Exception {
		// given
		String path = "/v1/post/1/comment";
		PostCommentRequestDto postCommentRequestDto = new PostCommentRequestDto(null, commentContent);
		when(postCommentService.save(anyLong(), anyLong(), any(PostCommentRequestDto.class))).thenReturn(id);

		// when
		ResultActions ra = mvc.perform(post(path).with(csrf()).with(authentication(authentication))
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(postCommentRequestDto)));

		// then
		ra.andExpect(status().isCreated()).andExpect(header().exists(HttpHeaders.LOCATION));
	}

	@WithMockUser
	@Test
	public void testGetPostConrtoller() throws Exception {
		// given
		String path = "/v1/post/1/comment/1";
		PostCommentResponseDto postCommentResponseDtoSub = new PostCommentResponseDto(id, "name",
				commentContent + "sub", null);
		List<PostCommentResponseDto> child = new ArrayList<>();
		child.add(postCommentResponseDtoSub);
		PostCommentResponseDto postCommentResponseDto = new PostCommentResponseDto(id, "name", commentContent, child);
		when(postCommentService.findById(id)).thenReturn(postCommentResponseDto);

		// when
		ResultActions ra = mvc.perform(get(path).with(csrf()).with(authentication(authentication)));

		// then
		ra.andExpect(status().isOk()).andExpect(jsonPath("$.content").value(commentContent));
		ra.andExpect(status().isOk()).andExpect(jsonPath("$.childList[0].content").value(commentContent + "sub"));

	}

	public void testPutPostCommentController() throws Exception {
		// given
		String path = "/v1/post/1/comment/1";
		UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(1L, "test@test.com", Role.USER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userAuthenticationDto, null,
				Collections.singleton(new SimpleGrantedAuthority(userAuthenticationDto.getRole().getKey())));

		String updatedComment = "updated comment";
		PostCommentUpdateRequestDto postCommentRequestDto = new PostCommentUpdateRequestDto(updatedComment);

		// when
		ResultActions ra = mvc.perform(put(path).with(csrf()).with(authentication(authentication))
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(postCommentRequestDto)));

		// then
		ra.andExpect(status().isNoContent());

	}

	@Test
	public void testDeletePostCommentController() throws Exception {
		// given
		String path = "/v1/post/1/comment/1";
		UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(1L, "test@test.com", Role.USER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userAuthenticationDto, null,
				Collections.singleton(new SimpleGrantedAuthority(userAuthenticationDto.getRole().getKey())));

		// when
		ResultActions ra = mvc.perform(delete(path).with(csrf()).with(authentication(authentication)));

		// then
		ra.andExpect(status().isNoContent());

	}

}
