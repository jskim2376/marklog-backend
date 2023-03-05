package com.marklog.blog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
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
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.controller.dto.UserResponseDto;
import com.marklog.blog.controller.dto.UserUpdateRequestDto;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.service.UserService;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = { UserController.class, JwtTokenProvider.class })
public class UserControllerTest {
	@Autowired
	private MockMvc mvc;
	@MockBean
	private UserService userService;

	String path = "/v1/user/";
	Long id = 1L;
	Long notFoundId = 0L;
	String name = "name";
	String email = "test@gmail.com";
	String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
	String title = "title";
	String introduce = "introduce";
	User user;
	UserResponseDto userResponseDto;

	@BeforeEach
	public void setUp() {
		user = new User(name, email, picture, title, introduce, Role.USER);
		userResponseDto = new UserResponseDto(user);
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@WithMockUser(roles = "USER")
	@Test
	public void testGetAllUserConrtoller() throws Exception {
		// given
		List<UserResponseDto> content = new ArrayList<>();
		content.add(userResponseDto);
		int pageCount = 0;
		int size = 20;
		Pageable pageable = PageRequest.of(pageCount, size);
		Page<UserResponseDto> page = new PageImpl<>(content, pageable, 1);

		when(userService.findAll(pageable)).thenReturn(page);

		// when
		ResultActions ra = mvc.perform(get("/v1/user"));
		// then
		ra.andExpect(status().isOk()).andExpect(jsonPath("$.size").value(20))
				.andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.content[0].email").value(email));
	}

	@WithMockUser(roles = "USER")
	@Test
	public void testGetUserConrtoller() throws Exception {
		// given
		String path = this.path + this.id;
		when(userService.findById(1L)).thenReturn(userResponseDto);

		// when
		ResultActions ra = mvc.perform(get(path));
		// then
		ra.andExpect(status().isOk()).andExpect(jsonPath("$.email").value(email))
				.andExpect(jsonPath("$.name").value(name)).andExpect(jsonPath("$.picture").value(picture))
				.andExpect(jsonPath("$.title").value(title)).andExpect(jsonPath("$.introduce").value(introduce));
	}

	@WithMockUser(roles = "USER")
	@Test
	public void testGetUserConrtoller_not_found() throws Exception {
		// given
		String path = this.path + this.notFoundId;
		when(userService.findById(notFoundId)).thenThrow(NoSuchElementException.class);

		// when
		ResultActions ra = mvc.perform(get(path));
		// then
		ra.andExpect(status().isNotFound());
	}

	@WithMockUser(roles = "ADMIN")
	@Test
	public void testPutUserController() throws Exception {
		// given
		String path = this.path + this.id;
		String putName = "name2";
		String putPicture = "http://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String putTitle = "title2";
		String putIntroduce = "hello world";
		UserUpdateRequestDto updateRequestDto = new UserUpdateRequestDto(putName, putPicture, putTitle, putIntroduce);

		// when
		ResultActions ra = mvc.perform(
				put(path).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(asJsonString(updateRequestDto)));
		// then
		ra.andExpect(status().isNoContent());
	}

	@WithMockUser(roles = "ADMIN")
	@Test
	public void testPutUserController_not_found() throws Exception {
		// given
		String path = this.path + this.notFoundId;
		String putName = "name2";
		String putPicture = "http://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String putTitle = "title2";
		String putIntroduce = "hello world";
		UserUpdateRequestDto updateRequestDto = new UserUpdateRequestDto(putName, putPicture, putTitle, putIntroduce);

		doThrow(NoSuchElementException.class).when(userService).update(eq(notFoundId), any(UserUpdateRequestDto.class));

		// when
		ResultActions ra = mvc.perform(
				put(path).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(asJsonString(updateRequestDto)));
		// then
		ra.andExpect(status().isNotFound());
	}

	@WithMockUser(roles = "ADMIN")
	@Test
	public void testDeleteUserController() throws Exception {
		// given
		String path = this.path + this.id;

		// when
		ResultActions ra = mvc.perform(delete(path).with(csrf()));
		// then
		ra.andExpect(status().isNoContent());
	}

	@WithMockUser(roles = "ADMIN")
	@Test
	public void testDeleteUserController_not_found() throws Exception {
		// given
		String path = this.path + this.notFoundId;
		doThrow(EmptyResultDataAccessException.class).when(userService).delete(notFoundId);

		// when
		ResultActions ra = mvc.perform(delete(path).with(csrf()));
		// then
		ra.andExpect(status().isNotFound());
	}
}
