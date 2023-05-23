package com.marklog.blog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.tag.Tag;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.dto.PostListResponseDto;
import com.marklog.blog.dto.PostResponseDto;
import com.marklog.blog.dto.PostSaveRequestDto;
import com.marklog.blog.dto.PostUpdateRequestDto;
import com.marklog.blog.dto.TagResponseDto;
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

	Long userId = 1L;
	String userName = "name";
	Authentication authentication;

	String path = "/v1/post/";

	Long postId = 1L;
	String title = "title";
	String thumbnail = "thumbnail";
	String summary = "summary";
	String picture="picture";
	String content = "content";
	LocalDateTime time;
	
	String tagName = "tagName";
	
	PostResponseDto postResponseDto;
	PostListResponseDto postListResponseDto;
	Page<PostListResponseDto> page;

	@BeforeEach
	public void setUp() {
		time = LocalDateTime.now();
		
		UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(1L, "test@test.com", Role.USER);
		authentication = new UsernamePasswordAuthenticationToken(userAuthenticationDto, null,
				Collections.singleton(new SimpleGrantedAuthority(userAuthenticationDto.getRole().getKey())));

		List<TagResponseDto> tagResponseDtos = new ArrayList<>();
		TagResponseDto tagResponseDto = new TagResponseDto(tagName);
		tagResponseDtos.add(tagResponseDto);
		
		postResponseDto = new PostResponseDto(time, time, title, content, userId, userName,
				tagResponseDtos, null);
		postListResponseDto = new PostListResponseDto(postId, thumbnail, title, summary, time, 0, 0, picture, userName, userId, tagResponseDtos);

		List<PostListResponseDto> content = new ArrayList<>();
		PostListResponseDto postListResponseDto = new PostListResponseDto(postId, thumbnail, title, summary, time,
				0, 0,  picture, userName, userId, null);
		content.add(postListResponseDto);

		int pageCount = 0;
		int size = 20;
		Pageable pageable = PageRequest.of(pageCount, size, Sort.by("id").descending());

		
		page = new PageImpl<>(content, pageable, 1);

	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@WithMockUser
	@Test
	public void testGetRecentPostConrtoller() throws Exception {
		// given
		when(postService.recentPost(any(Pageable.class))).thenReturn(page);

		// when
		ResultActions ra = mvc.perform(get("/v1/post/recent").with(csrf()));
		// then
		ra.andExpect(status().isOk()).andExpect(jsonPath("$.size").value(20))
				.andExpect(jsonPath("$.totalElements").value(1))
				.andExpect(jsonPath("$.content[0].postId").value(postId))
				.andExpect(jsonPath("$.content[0].thumbnail").value("thumbnail"))
				.andExpect(jsonPath("$.content[0].title").value(title))
				.andExpect(jsonPath("$.content[0].summary").value("summary"))
				.andExpect(jsonPath("$.content[0].commentCount").value(0))
				.andExpect(jsonPath("$.content[0].likeCount").value(0))
				.andExpect(jsonPath("$.content[0].picture").value("picture"))
				.andExpect(jsonPath("$.content[0].userName").value(userName))
				.andExpect(jsonPath("$.content[0].userId").value(userId));

	}

	@WithMockUser
	@Test
	public void testSearchPostConrtoller() throws Exception {
		// given
		String keywords = "search keyword";
		when(postService.search(any(Pageable.class), any(String[].class))).thenReturn(page);

		// when
		ResultActions ra = mvc.perform(get("/v1/post/search").param("text", keywords).with(csrf()));

		// then
		ra.andExpect(status().isOk()).andExpect(jsonPath("$.size").value(20))
				.andExpect(jsonPath("$.totalElements").value(1))
				.andExpect(jsonPath("$.content[0].postId").value(postId))
				.andExpect(jsonPath("$.content[0].thumbnail").value("thumbnail"))
				.andExpect(jsonPath("$.content[0].title").value(title))
				.andExpect(jsonPath("$.content[0].summary").value("summary"))
				.andExpect(jsonPath("$.content[0].commentCount").value(0))
				.andExpect(jsonPath("$.content[0].likeCount").value(0))
				.andExpect(jsonPath("$.content[0].picture").value("picture"))
				.andExpect(jsonPath("$.content[0].userName").value(userName))
				.andExpect(jsonPath("$.content[0].userId").value(userId));
	}
	
	@WithMockUser
	@Test
	public void testTagNamePostConrtoller() throws Exception {
		// given
		List<PostListResponseDto> postListResponseDtos = new ArrayList<>();
		postListResponseDtos.add(postListResponseDto);
		when(postService.findAllByTagName(any(Pageable.class), eq(tagName))).thenReturn(postListResponseDtos);

		// when
		ResultActions ra = mvc.perform(get("/v1/post/tag").param("tag-name", tagName).with(csrf()));

		// then
		ra.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].postId").value(postId))
		.andExpect(jsonPath("$[0].thumbnail").value(thumbnail))
		.andExpect(jsonPath("$[0].title").value(title))
		.andExpect(jsonPath("$[0].summary").value(summary))
		.andExpect(jsonPath("$[0].commentCount").value(0))
		.andExpect(jsonPath("$[0].likeCount").value(0))
		.andExpect(jsonPath("$[0].picture").value(picture))
		.andExpect(jsonPath("$[0].userName").value(userName))
		.andExpect(jsonPath("$[0].userId").value(userId));
	}
	
	@WithMockUser
	@Test
	public void testTagNameAndUserIdPostConrtoller() throws Exception {
		// given
		List<PostListResponseDto> postListResponseDtos = new ArrayList<>();
		postListResponseDtos.add(postListResponseDto);
		
		when(postService.findAllByTagNameAndUserId(any(Pageable.class),  eq(tagName),  eq(userId))).thenReturn(postListResponseDtos);

		// when
		ResultActions ra = mvc.perform(get("/v1/post/tag").param("tag-name", tagName).param("user-id", userId.toString()).with(csrf()));

		// then
		ra.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].postId").value(postId))
		.andExpect(jsonPath("$[0].thumbnail").value(thumbnail))
		.andExpect(jsonPath("$[0].title").value(title))
		.andExpect(jsonPath("$[0].summary").value(summary))
		.andExpect(jsonPath("$[0].commentCount").value(0))
		.andExpect(jsonPath("$[0].likeCount").value(0))
		.andExpect(jsonPath("$[0].picture").value(picture))
		.andExpect(jsonPath("$[0].userName").value(userName))
		.andExpect(jsonPath("$[0].userId").value(userId));
	}

	@Test
	public void testPostPostConrtoller() throws Exception {
		// given
		List<String> tagList = new ArrayList<>();
		tagList.add("java");
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(path, path, tagList);
		when(postService.save(anyLong(), any())).thenReturn(postId);

		// when
		ResultActions ra = mvc.perform(post(path).with(csrf()).with(authentication(authentication))
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(postSaveRequestDto)));
		// then
		ra.andExpect(status().isCreated()).andExpect(header().exists(HttpHeaders.LOCATION));
	}

	@WithMockUser
	@Test
	public void testGetPostConrtoller() throws Exception {
		// given
		String path = this.path + postId;
		when(postService.findById(postId)).thenReturn(postResponseDto);
		when(postLikeService.findById(postId, userId)).thenReturn(true);

		// when
		ResultActions ra = mvc.perform(get(path).with(csrf()).with(authentication(authentication)));

		// then
		ra.andExpect(status().isOk())
				.andExpect(jsonPath("$.createdDate").value(time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
				.andExpect(jsonPath("$.modifiedDate").value(time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
				.andExpect(jsonPath("$.title").value(title)).andExpect(jsonPath("$.content").value(content))
				.andExpect(jsonPath("$.userId").value(1L)).andExpect(jsonPath("$.like").value(true));
	}

	@WithMockUser
	@Test
	public void testGetPostConrtoller_not_found() throws Exception {
		// given
		String path = this.path + postId;
		when(postService.findById(postId)).thenThrow(NoSuchElementException.class);

		// when
		ResultActions ra = mvc.perform(get(path).with(csrf()).with(authentication(authentication)));

		// then
		ra.andExpect(status().isNotFound());
	}

	@Test
	public void testPutPostConrtoller() throws Exception {
		// given
		String path = this.path + postId;

		String newTitle = "title2";
		String newConntent = "content2";
		List<String> tagList = new ArrayList<>();
		tagList.add("java");
		PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(newTitle, newConntent, tagList);

		when(postService.findById(postId)).thenReturn(postResponseDto);

		// when
		ResultActions ra = mvc.perform(put(path).with(csrf()).with(authentication(authentication))
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(postUpdateRequestDto)));

		// then
		ra.andExpect(status().isNoContent()).andExpect(header().exists(HttpHeaders.LOCATION));
	}

	@Test
	public void testPutPostConrtoller_not_found() throws Exception {
		// given
		String path = this.path + postId;

		String newTitle = "title2";
		String newConntent = "content2";
		List<String> tagList = new ArrayList<>();
		tagList.add("java");
		PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(newTitle, newConntent, tagList);

		doThrow(NoSuchElementException.class).when(postService).update(eq(postId), any(PostUpdateRequestDto.class));

		// when
		ResultActions ra = mvc.perform(put(path).with(csrf()).with(authentication(authentication))
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(postUpdateRequestDto)));

		// then
		ra.andExpect(status().isNotFound());
	}

	@Test
	public void testDeletePostController() throws Exception {
		// given
		String path = this.path + postId;

		// when
		ResultActions ra = mvc.perform(delete(path).with(csrf()).with(authentication(authentication)));

		// then
		ra.andExpect(status().isNoContent());
	}

	@Test
	public void testDeletePostController_not_found() throws Exception {
		// given
		String path = this.path + postId;
		doThrow(EmptyResultDataAccessException.class).when(postService).delete(postId);

		// when
		ResultActions ra = mvc.perform(delete(path).with(csrf()).with(authentication(authentication)));

		// then
		ra.andExpect(status().isNotFound());
	}

}
