package com.marklog.blog.domain.post;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.config.auth.dto.UserAuthenticationDto;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.service.PostService;
import com.marklog.blog.web.PostController;
import com.marklog.blog.web.dto.PostResponseDto;
import com.marklog.blog.web.dto.PostSaveRequestDto;
import com.marklog.blog.web.dto.PostUpdateRequestDto;
import com.marklog.blog.web.dto.UserResponseDto;
import com.marklog.blog.web.dto.UserUpdateRequestDto;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = PostController.class)
@ContextConfiguration(classes = { PostController.class, JwtTokenProvider.class })
public class PostControllerTest {
	@Autowired
	private MockMvc mvc;

	@MockBean
	private PostService postService;

	Long id = 1L;
	String title = "title";
	String content = "content";
	static LocalDateTime time;

	@BeforeAll
	public static void setUp() {
		time = LocalDateTime.now();
	}

	@Test
	public void testPostPostConrtoller() throws Exception {
		// given
		String path = "/v1/post";
		List<String> tagList = new ArrayList<>();
		tagList.add("java");
		tagList.add("testTag");
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(path, path, tagList);
		when(postService.save(anyLong(), any())).thenReturn(id);

		UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(1L, "test@test.com", Role.USER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userAuthenticationDto, null, Collections.singleton(new SimpleGrantedAuthority(userAuthenticationDto.getRole().getKey())));
		// when
		ResultActions ra = mvc.perform(post(path)
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(postSaveRequestDto)));

		// then
		ra.andExpect(status().isCreated());
	}

	@WithMockUser(roles = "USER")
	@Test
	public void testGetAllUserConrtoller() throws Exception {
		// given
		PostResponseDto postResponseDto = new PostResponseDto(time, time, title, content, id);
		List<PostResponseDto> content = new ArrayList<>();
		content.add(postResponseDto);

		int pageCount = 0;
		int size = 20;
		Pageable pageable = PageRequest.of(pageCount, size);
		Page<PostResponseDto> page = new PageImpl<>(content, pageable, 1);
		when(postService.findAll(pageable)).thenReturn(page);

		// when
		ResultActions ra = mvc.perform(get("/v1/post"));
		// then
		ra.andExpect(status().isOk()).andExpect(jsonPath("$.size").value(20))
				.andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.content[0].title").value(title));

	}

	@WithMockUser(roles = "USER")
	@Test
	public void testGetPostConrtoller() throws Exception {
		// given
		String path = "/v1/post/" + id;
		PostResponseDto postResponseDto = new PostResponseDto(time, time, title, content, 1L);
		when(postService.findById(anyLong())).thenReturn(postResponseDto);

		// when
		ResultActions ra = mvc.perform(get(path));

		// then
		ra.andExpect(status().isOk())
				.andExpect(jsonPath("$.createdDate").value(time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
				.andExpect(jsonPath("$.modifiedDate").value(time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
				.andExpect(jsonPath("$.title").value(title)).andExpect(jsonPath("$.content").value(content))
				.andExpect(jsonPath("$.userId").value(1L));
	}

	@Test
	public void testPutPostConrtoller() throws Exception {
		// given
		String path = "/v1/post/" + id;
		String title2 = "title2";
		String content2 = "content2";
		List<String> tagList = new ArrayList<>();
		tagList.add("java");
		tagList.add("testTag");
		PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(title2, content2, tagList);
		PostResponseDto postResponseDto = new PostResponseDto(time, time, title2, content2, id);
		when(postService.findById(id)).thenReturn(postResponseDto);
		UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(1L, "test@test.com", Role.USER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userAuthenticationDto, null, Collections.singleton(new SimpleGrantedAuthority(userAuthenticationDto.getRole().getKey())));

		// when
		ResultActions ra = mvc.perform(put(path).with(SecurityMockMvcRequestPostProcessors.csrf()).with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(postUpdateRequestDto)));

		// then
		ra.andExpect(status().isNoContent());
	}

	@WithMockUser(roles = "ADMIN")
	@Test
	public void testDeletePostController() throws Exception {
		// given
		String path = "/v1/post/1";

		// when
		ResultActions ra = mvc.perform(delete(path).with(SecurityMockMvcRequestPostProcessors.csrf()));

		// then
		ra.andExpect(status().isNoContent());
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
