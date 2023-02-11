package com.marklog.blog.web.dto;

import com.marklog.blog.domain.post.Post;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class PostResponseDto {
	private final Long id;
	private final String title;
	private final String content;

	public PostResponseDto(Post entity) {
		this.id = entity.getId();
		this.title = entity.getTitle();
		this.content = entity.getContent();
	}
}
