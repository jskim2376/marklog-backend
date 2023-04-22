package com.marklog.blog.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.dto.TagCountResponseDto;
import com.marklog.blog.service.TagService;

@WebMvcTest(controllers = TagController.class)
@ContextConfiguration(classes = { TagController.class, JwtTokenProvider.class })
public class TagControllerTest {
	@Autowired
	private MockMvc mvc;
	@MockBean
	private TagService tagService;

	@WithMockUser(roles = "USER")
	@Test
	public void testTagNameCount() throws Exception {
		// given
		Long userId = 1L;
		String path = "/v1/tag/"+userId;
		String tagName = "tagname";
		TagCountResponseDto tagCountResponseDto = new TagCountResponseDto(tagName, 1L);
		List<TagCountResponseDto> tagCountResponseDtos = new ArrayList<>();
		tagCountResponseDtos.add(tagCountResponseDto);
		// when
		ResultActions ra = mvc.perform(get(path).with(csrf()));

		// then
		ra.andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value(tagName)).andExpect(jsonPath("$[0].count").value(1));

	}


}
