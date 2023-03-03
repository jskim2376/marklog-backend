package com.marklog.blog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
import org.springframework.http.HttpHeaders;
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
import com.marklog.blog.controller.dto.PostResponseDto;
import com.marklog.blog.controller.dto.PostSaveRequestDto;
import com.marklog.blog.controller.dto.PostUpdateRequestDto;
import com.marklog.blog.controller.dto.TagResponseDto;
import com.marklog.blog.domain.tag.Tag;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.service.PostLikeService;
import com.marklog.blog.service.PostService;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = PostController.class)
@ContextConfiguration(classes = { PostController.class, JwtTokenProvider.class })
public class PostControllerTest {
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private PostService postService;

	@MockBean
	private PostLikeService postLikeService;

	static LocalDateTime time;
	Long postId = 1L;
	String title = "title";
	String content = "content";
	Long userId = 1L;
	String userName = "name";
	
	@BeforeAll
	public static void setUp() {
		time = LocalDateTime.now();
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testPostPostConrtoller() throws Exception {
		// given
		String path = "/v1/post";
		List<String> tagList = new ArrayList<>();
		tagList.add("java");
		tagList.add("testTag");
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(path, path, tagList);
		when(postService.save(anyLong(), any())).thenReturn(postId);

		UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(1L, "test@test.com", Role.USER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userAuthenticationDto, null, Collections.singleton(new SimpleGrantedAuthority(userAuthenticationDto.getRole().getKey())));
		// when
		ResultActions ra = mvc.perform(post(path)
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(postSaveRequestDto)));
		// then
		ra.andExpect(status().isCreated()).andExpect(header().exists(HttpHeaders.LOCATION));
	}

	@WithMockUser
	@Test
	public void testGetAllPostConrtoller() throws Exception {
		// given
		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag(null, "tag1"));
		tags.add(new Tag(null, "tag2"));

		PostResponseDto postResponseDto = new PostResponseDto(time, time, title, content, userId, userName, TagResponseDto.toEntityDto(tags), null);
		List<PostResponseDto> content = new ArrayList<>();
		content.add(postResponseDto);

		int pageCount = 0;
		int size = 20;
		Pageable pageable = PageRequest.of(pageCount, size);
		Page<PostResponseDto> page = new PageImpl<>(content, pageable, 1);
		when(postService.findAll(pageable)).thenReturn(page);

		// when
		ResultActions ra = mvc.perform(get("/v1/post").with(SecurityMockMvcRequestPostProcessors.csrf()));
		// then
		ra.andExpect(status().isOk()).andExpect(jsonPath("$.size").value(20))
				.andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.content[0].title").value(title));

	}
	
	@WithMockUser
	@Test
	public void testSearchPostConrtoller() throws Exception {
		// given
		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag(null, "tag1"));
		tags.add(new Tag(null, "tag2"));

		PostResponseDto postResponseDto = new PostResponseDto(time, time, title, content, userId, userName, TagResponseDto.toEntityDto(tags), null);
		List<PostResponseDto> content = new ArrayList<>();
		content.add(postResponseDto);

		int pageCount = 0;
		int size = 20;
		Pageable pageable = PageRequest.of(pageCount, size);
		Page<PostResponseDto> page = new PageImpl<>(content, pageable, 1);
		String text="search keyword";
		when(postService.search(pageable, text.split(" "))).thenReturn(page);

		// when
		ResultActions ra = mvc.perform(get("/v1/post/search").param("text", text).with(SecurityMockMvcRequestPostProcessors.csrf()));

		// then
		ra.andExpect(status().isOk()).andExpect(jsonPath("$.size").value(20))
				.andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.content[0].title").value(title));

	}

	@WithMockUser
	@Test
	public void testGetPostConrtoller() throws Exception {
		// given
		String path = "/v1/post/" + postId;
		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag(null, "tag2"));

		PostResponseDto postResponseDto = new PostResponseDto(time, time, title, content, userId, userName, TagResponseDto.toEntityDto(tags), null);
		when(postService.findById(anyLong())).thenReturn(postResponseDto);
		when(postLikeService.findById(postId, userId)).thenReturn(true);

		UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(userId, "test@test.com", Role.USER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userAuthenticationDto, null, Collections.singleton(new SimpleGrantedAuthority(userAuthenticationDto.getRole().getKey())));
		// when
		ResultActions ra = mvc.perform(get(path).with(SecurityMockMvcRequestPostProcessors.csrf()).with(SecurityMockMvcRequestPostProcessors.authentication(authentication)));

		// then
		ra.andExpect(status().isOk())
				.andExpect(jsonPath("$.createdDate").value(time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
				.andExpect(jsonPath("$.modifiedDate").value(time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
				.andExpect(jsonPath("$.title").value(title)).andExpect(jsonPath("$.content").value(content))
				.andExpect(jsonPath("$.userId").value(1L)).andExpect(jsonPath("$.like").value(true));
	}

	@Test
	public void testPutPostConrtoller() throws Exception {
		// given
		String path = "/v1/post/" + postId;
		String title2 = "title2";
		String content2 = "content2";

		List<String> tagList = new ArrayList<>();
		tagList.add("java");
		tagList.add("testTag");
		PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(title2, content2, tagList);

		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag(null, "tag1"));
		tags.add(new Tag(null, "tag2"));

		PostResponseDto postResponseDto = new PostResponseDto(time, time, title, content, userId, userName, TagResponseDto.toEntityDto(tags), null);
		when(postService.findById(postId)).thenReturn(postResponseDto);

		UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(1L, "test@test.com", Role.USER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userAuthenticationDto, null, Collections.singleton(new SimpleGrantedAuthority(userAuthenticationDto.getRole().getKey())));

		// when
		ResultActions ra = mvc.perform(put(path).with(SecurityMockMvcRequestPostProcessors.csrf()).with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(postUpdateRequestDto)));

		// then
		ra.andExpect(status().isNoContent()).andExpect(header().exists(HttpHeaders.LOCATION));
	}

	@Test
	public void testDeletePostController() throws Exception {
		// given
		String path = "/v1/post/1";
		UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(1L, "test@test.com", Role.USER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userAuthenticationDto, null, Collections.singleton(new SimpleGrantedAuthority(userAuthenticationDto.getRole().getKey())));


		// when
		ResultActions ra = mvc.perform(delete(path)
				.with(SecurityMockMvcRequestPostProcessors.csrf())
				.with(SecurityMockMvcRequestPostProcessors.authentication(authentication)));

		// then
		ra.andExpect(status().isNoContent());
	}

}
