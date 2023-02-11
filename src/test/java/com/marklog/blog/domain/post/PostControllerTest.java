package com.marklog.blog.domain.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklog.blog.service.PostService;
import com.marklog.blog.service.UserService;
import com.marklog.blog.web.PostController;
import com.marklog.blog.web.UserController;
import com.marklog.blog.web.dto.PostResponseDto;
import com.marklog.blog.web.dto.PostSaveRequestDto;
import com.marklog.blog.web.dto.PostUpdateRequestDto;
import com.marklog.blog.web.dto.UserResponseDto;
import com.marklog.blog.web.dto.UserUpdateRequestDto;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = PostController.class)
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
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.id").value(id));
	}
	
	@WithMockUser(roles="USER")
	@Test
	public void testGetPostConrtoller() throws Exception {
		//given
		String path = "/v1/post/"+id;
		PostResponseDto postResponseDto = new PostResponseDto(time, time, title, content);
		when(postService.findById(anyLong())).thenReturn(postResponseDto);
		System.out.println(time);
		//when
		ResultActions ra = mvc.perform(get(path));
		//then
		System.out.println(time.toString());
		ra.andExpect(status().isOk())
		.andExpect(jsonPath("$.createdDate").value(time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
		.andExpect(jsonPath("$.modifiedDate").value(time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
		.andExpect(jsonPath("$.title").value(title))
		.andExpect(jsonPath("$.content").value(content));
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
		when(postService.update(id, postUpdateRequestDto)).thenReturn(id);

		//when
		ResultActions ra = mvc.perform(
			put(path)
			.with(SecurityMockMvcRequestPostProcessors.csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(postUpdateRequestDto))
		);
		//then
		ra.
		andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(id));
	}
	
	@WithMockUser(roles="ADMIN")
	@Test
	public void testDeletePostController() throws Exception {
		//given
		String path = "/v1/post/1";

		//when
		//then
		mvc.perform(
				delete(path).with(SecurityMockMvcRequestPostProcessors.csrf())
		)
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
