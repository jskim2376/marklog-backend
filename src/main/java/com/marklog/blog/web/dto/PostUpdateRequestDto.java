package com.marklog.blog.web.dto;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class PostUpdateRequestDto {
	private String title;
	private String content;
	private List<String> tagNames;

	@Builder
	public PostUpdateRequestDto(String title, String content, List<String> tagNames) {
		this.title = title;
		this.content = content;
		this.tagNames = tagNames;
	}
}
