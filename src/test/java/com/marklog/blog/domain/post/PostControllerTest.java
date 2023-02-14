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
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.service.PostService;
import com.marklog.blog.web.PostController;
import com.marklog.blog.web.dto.PostResponseDto;
import com.marklog.blog.web.dto.PostSaveRequestDto;
import com.marklog.blog.web.dto.PostUpdateRequestDto;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = PostController.class)
@ContextConfiguration(classes = {PostController.class, JwtTokenProvider.class})
public class PostControllerTest {
	@Autowired
	private MockMvc mvc;

	@MockBean
	private PostService postService;

	Long id=1L;
	String title = "title";
	String content = "content";
	static LocalDateTime time;

	@BeforeAll
	public static void setUp() {
		time = LocalDateTime.now();
	}

	@WithMockUser(roles="USER")
	@Test
	public void testPostPostConrtoller() throws Exception {
		//given
		String path = "/v1/post";
		List<String> tagList = new ArrayList<>();
		tagList.add("java");
		tagList.add("testTag");
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(path, path, id, tagList);
		when(postService.save(any())).thenReturn(id);

		//when
		ResultActions ra = mvc.perform(
				post(path).with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(postSaveRequestDto))
		);

		//then
		ra
		.andExpect(status().isCreated());
	}

	@WithMockUser(roles="USER")
	@Test
	public void testGetPostConrtoller() throws Exception {
		//given
		String path = "/v1/post/"+id;
		PostResponseDto postResponseDto = new PostResponseDto(time, time, title, content, 1L);
		when(postService.findById(anyLong())).thenReturn(postResponseDto);

		//when
		ResultActions ra = mvc.perform(get(path));

		//then
		ra.andExpect(status().isOk())
		.andExpect(jsonPath("$.createdDate").value(time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
		.andExpect(jsonPath("$.modifiedDate").value(time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
		.andExpect(jsonPath("$.title").value(title))
		.andExpect(jsonPath("$.content").value(content))
		.andExpect(jsonPath("$.userId").value(1L));
	}

	@WithMockUser(roles="USER")
	@Test
	public void testPutPostConrtoller() throws Exception {
		//given
		String path = "/v1/post/"+id;
		String title2 = "title2";
		String content2 = "content2";
		List<String> tagList = new ArrayList<>();
		tagList.add("java");
		tagList.add("testTag");
		PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(title2, content2, tagList);
		PostResponseDto postResponseDto = new PostResponseDto(time, time, title2, content2, id);
		when(postService.findById(id)).thenReturn(postResponseDto);

		//when
		ResultActions ra = mvc.perform(
			put(path)
			.with(SecurityMockMvcRequestPostProcessors.csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(postUpdateRequestDto))
		);

		//then
		ra.
		andExpect(status().isNoContent());
	}

	@WithMockUser(roles="ADMIN")
	@Test
	public void testDeletePostController() throws Exception {
		//given
		String path = "/v1/post/1";

		//when
		ResultActions ra = mvc.perform(
				delete(path).with(SecurityMockMvcRequestPostProcessors.csrf())
		);

		//then
		ra
		.andExpect(status().isNoContent());
	}


	public static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
}

