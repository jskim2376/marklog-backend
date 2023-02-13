package com.marklog.blog.domain.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.service.UserService;
import com.marklog.blog.web.UserController;
import com.marklog.blog.web.dto.UserResponseDto;
import com.marklog.blog.web.dto.UserUpdateRequestDto;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = {UserController.class, JwtTokenProvider.class})
public class UserControllerTest {
	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserService userService;

	public static LocalDateTime time;
	public static String name = "name";
	public static String email = "test@gmail.com";
	public static String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
	public static String title = "title";
	public static String introduce = "introduce";

	@BeforeAll
	public static void setUp() {
		time = LocalDateTime.now();

	}

	@WithMockUser(roles="USER")
	@Test
	public void testGetUserConrtoller() throws Exception {
		//given
		String path = "/v1/user/1";
		UserResponseDto userResponseDto = new UserResponseDto(time, time, email, name, picture, title, introduce);
		Mockito.when(userService.findById(1L)).thenReturn(userResponseDto);

		//when
		//then
		mvc.perform(
			get(path)
		)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.email").value(email))
		.andExpect(jsonPath("$.name").value(name))
		.andExpect(jsonPath("$.picture").value(picture))
		.andExpect(jsonPath("$.title").value(title))
		.andExpect(jsonPath("$.introduce").value(introduce));
	}

	@WithMockUser(roles="ADMIN")
	@Test
	public void testPutUserController() throws Exception {
		//given
		String path = "/v1/user/1";
		String putName = "name2";
		String putPicture = "http://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String putTitle = "title2";
		String putIntroduce = "hello world";

		UserResponseDto userResponseDto = new UserResponseDto(time, time, email, putName, putPicture, putTitle, putIntroduce);
		UserUpdateRequestDto updateRequestDto = new UserUpdateRequestDto(putName, putPicture, putTitle, putIntroduce);
		Mockito.when(userService.update(1L, updateRequestDto)).thenReturn(userResponseDto);

		//when
		//then
		mvc.perform(
			put(path)
		.		with(SecurityMockMvcRequestPostProcessors.csrf()
			)
			.contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(updateRequestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value(putName))
			.andExpect(jsonPath("$.picture").value(putPicture))
			.andExpect(jsonPath("$.title").value(putTitle))
			.andExpect(jsonPath("$.introduce").value(putIntroduce));

	}

	@WithMockUser(roles="ADMIN")
	@Test
	public void testDeleteUserController() throws Exception {
		//given
		String path = "/v1/user/1";

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

