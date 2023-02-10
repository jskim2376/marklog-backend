package com.marklog.blog.domain.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklog.blog.service.UserService;
import com.marklog.blog.web.UserController;
import com.marklog.blog.web.dto.UserResponseDto;
import com.marklog.blog.web.dto.UserUpdateRequestDto;
@ExtendWith(SpringExtension.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private UserService userService;

	@MockBean
	private UsersRepository usersRepository;

	LocalDateTime time;
	String name;
	String email;
	String picture;
	String title;
	String introduce;
	
	@BeforeEach
	public void setUp() {
		time = LocalDateTime.now();
		email = "test@gmail.com";
		name = "name";
		picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		title = "title";
		introduce = "introduce";
	}
	
	@Test	
	@WithMockUser(roles= {"ADMIN"})
	public void userGetControllerTest() throws Exception {
		//given
		String path = "/v1/user/1";
		UserResponseDto userResponseDto = new UserResponseDto(time, time, email, name, picture, title, introduce);
		Mockito.when(userService.findById(1L)).thenReturn(userResponseDto);
		
		//when
		//then
		mvc.perform(get(path))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.email").value(email))
		.andExpect(jsonPath("$.name").value(name))
		.andExpect(jsonPath("$.picture").value(picture))
		.andExpect(jsonPath("$.title").value(title))
		.andExpect(jsonPath("$.introduce").value(introduce))
		.andDo(MockMvcResultHandlers.print());
	}
	
	@WithMockUser(roles= {"ADMIN"})
	@Test
	public void userPutTest() throws Exception {
		//given
		String path = "/v1/user/1";
		String putName = "name2";
		String putPicture = "http://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String putTitle = "title2";
		String putIntroduce = "hello world";
		UserResponseDto userResponseDto = new UserResponseDto(time, time, email, putName, putPicture, putTitle, putIntroduce);
		Mockito.when(userService.findById(1L)).thenReturn(userResponseDto);
		UserUpdateRequestDto updateRequestDto = UserUpdateRequestDto.builder().name(putName).picture(putPicture).title(putTitle).introduce(putIntroduce).build();
		
		Mockito.when(userService.update(1L, updateRequestDto)).thenReturn(userResponseDto);
		
		//when
		//then
		mvc.perform(put(path)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(updateRequestDto)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name").value(putName))
		.andExpect(jsonPath("$.picture").value(putPicture))
		.andExpect(jsonPath("$.title").value(putTitle))
		.andExpect(jsonPath("$.introduce").value(putIntroduce));

	}
	
	public static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}

}

