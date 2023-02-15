package com.marklog.blog.domain.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.service.UserService;
import com.marklog.blog.web.UserController;
import com.marklog.blog.web.dto.UserResponseDto;
import com.marklog.blog.web.dto.UserUpdateRequestDto;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = { UserController.class, JwtTokenProvider.class })
public class UserControllerTest {
	@Autowired
	private MockMvc mvc;
	@MockBean
	private UserService userService;
	@MockBean
	private UserRepository userRepository;

	public static LocalDateTime time;
	public static String name = "name";
	public static String email = "test@gmail.com";
	public static String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
	public static String title = "title";
	public static String introduce = "introduce";

	String path = "/v1/user/1";

	@BeforeAll
	public static void setUp() {
		time = LocalDateTime.now();
	}

	@WithMockUser(roles="USER")
	@Test
	public void testGetAllUserConrtoller() throws Exception {
		//given
		User user = new User(name, email, picture, title, introduce, Role.USER);
		UserResponseDto userResponseDto = new UserResponseDto(user);
		List<UserResponseDto> content = new ArrayList<>();
		content.add(userResponseDto);
		
		int pageCount = 0;
		int size=20;
		Pageable pageable = PageRequest.of(pageCount, size);
		Page<UserResponseDto> page = new PageImpl<>(content, pageable, 1);
		when(userService.findAll(pageable)).thenReturn(page);
		
		//when
		ResultActions ra = mvc.perform(get("/v1/user"));
		//then
		ra
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.size").value(20))
		.andExpect(jsonPath("$.totalElements").value(1))
		.andExpect(jsonPath("$.content[0].email").value(email));
		
	}

	@WithMockUser(roles = "USER")
	@Test
	public void testGetUserConrtoller() throws Exception {
		// given
		UserResponseDto userResponseDto = new UserResponseDto(time, time, email, name, picture, title, introduce);
		Mockito.when(userService.findById(1L)).thenReturn(userResponseDto);

		// when
		ResultActions ra = mvc.perform(get(path));
		// then
		ra.andExpect(status().isOk()).andExpect(jsonPath("$.email").value(email))
				.andExpect(jsonPath("$.name").value(name)).andExpect(jsonPath("$.picture").value(picture))
				.andExpect(jsonPath("$.title").value(title)).andExpect(jsonPath("$.introduce").value(introduce));
	}

	@WithMockUser(roles = "ADMIN")
	@Test
	public void testPutUserController() throws Exception {
		// given
		String putName = "name2";
		String putPicture = "http://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String putTitle = "title2";
		String putIntroduce = "hello world";

		UserUpdateRequestDto updateRequestDto = new UserUpdateRequestDto(putName, putPicture, putTitle, putIntroduce);

		// when
		ResultActions ra = mvc.perform(put(path).with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(updateRequestDto)));
		// then
		ra.andExpect(status().isNoContent());

	}

	@WithMockUser(roles = "ADMIN")
	@Test
	public void testDeleteUserController() throws Exception {
		// given
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
