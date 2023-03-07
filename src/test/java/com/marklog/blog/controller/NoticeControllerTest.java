package com.marklog.blog.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


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
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.config.auth.dto.UserAuthenticationDto;
import com.marklog.blog.controller.dto.NoticeResponseDto;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.service.NoticeService;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(controllers = NoticeController.class)
@ContextConfiguration(classes = { NoticeController.class, JwtTokenProvider.class })
public class NoticeControllerTest {
	@Autowired
	MockMvc mvc;

	@MockBean
	NoticeService noticeService;

	String path = "/v1/notice/";
	User user;
	Long userId = 1L;
	Authentication authentication;

	Long noticeId = 2L;
	String noticeContent = "content";

	@BeforeEach
	public void setup() {
		String name = "name";
		String email = "test@gmail.com";
		String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String userTitle = "myblog";
		String introduce = "introduce";
		user = new User(name, email, picture, userTitle, introduce, Role.USER);

		UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(userId, "test@test.com", Role.USER);
		authentication = new UsernamePasswordAuthenticationToken(userAuthenticationDto, null,
				Collections.singleton(new SimpleGrantedAuthority(userAuthenticationDto.getRole().getKey())));
	}

	@Test
	public void testGetAllUnCheckNotice() throws Exception {
		// given
		String path = this.path + userId + "/uncheck";
		List<NoticeResponseDto> notices = new ArrayList<>();
		NoticeResponseDto noticeResponseDto = new NoticeResponseDto(noticeId, noticeContent, false, userId);
		notices.add(noticeResponseDto);

		when(noticeService.findAllUnCheckNotice(userId)).thenReturn(notices);
		// when
		ResultActions ra = mvc.perform(get(path).with(csrf()).with(authentication(authentication)));

		// then
		ra.andExpect(status().isOk()).andExpect(jsonPath("$.[0].id").value(noticeId))
				.andExpect(jsonPath("$.[0].content").value(noticeContent))
				.andExpect(jsonPath("$.[0].checkFlag").value(false)).andExpect(jsonPath("$.[0].userId").value(userId));
	}

	@Test
	public void testGetAllUnCheckNotice_not_found() throws Exception {
		// given
		String path = this.path + userId + "/uncheck";
		List<NoticeResponseDto> notices = new ArrayList<>();
		NoticeResponseDto noticeResponseDto = new NoticeResponseDto(noticeId, noticeContent, false, userId);
		notices.add(noticeResponseDto);

		when(noticeService.findAllUnCheckNotice(userId)).thenReturn(new ArrayList<>());
		// when
		ResultActions ra = mvc.perform(get(path).with(csrf()).with(authentication(authentication)));

		// then
		ra.andExpect(status().isOk()).andExpect(content().string("[]"));
	}
	@Test
	public void testCheckNotice() throws Exception {
		// given
		String path = this.path + noticeId;

		// when
		mvc.perform(put(path).with(csrf()).with(authentication(authentication)));

		// then
		verify(noticeService).checkNoticeById(noticeId);
	}

	@Test
	public void testCheckNotice_not_found() throws Exception {
		// given
		String path = this.path + noticeId;
		doThrow(NoSuchElementException.class).when(noticeService).checkNoticeById(noticeId);
		// when
		ResultActions ra = mvc.perform(put(path).with(csrf()).with(authentication(authentication)));

		// then
		ra.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteNotice() throws Exception {
		// given
		String path = this.path + noticeId;

		// when
		mvc.perform(delete(path).with(csrf()).with(authentication(authentication)));

		// then
		verify(noticeService).deleteNotice(noticeId);
	}

	@Test
	public void testDeleteNotice_not_found() throws Exception {
		// given
		String path = this.path + noticeId;
		doThrow(EmptyResultDataAccessException.class).when(noticeService).deleteNotice(noticeId);

		// when
		ResultActions ra = mvc.perform(delete(path).with(csrf()).with(authentication(authentication)));

		// then
		ra.andExpect(status().isNotFound());
	}

}
