package com.marklog.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marklog.blog.domain.tag.TagRepository;
import com.marklog.blog.dto.TagCountResponseDto;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {
	@Mock
	TagRepository tagRepository;
	TagService tagService;
	Long userId = 1L;

	@BeforeEach
	public void setUp() {
		tagService = new TagService(tagRepository);

	}

	@Test
	public void testPostLikeSave() {
		// given
		List<TagCountResponseDto> tagCountResponseDtos = new ArrayList<>();
		when(tagRepository.countTagNameByUserId(userId)).thenReturn(tagCountResponseDtos);

		// when
		List<TagCountResponseDto> getTagCountResponseDtos = tagService.countTagNameByUserId(userId);

		// then
		assertThat(getTagCountResponseDtos).isEqualTo(tagCountResponseDtos);
	}

}
