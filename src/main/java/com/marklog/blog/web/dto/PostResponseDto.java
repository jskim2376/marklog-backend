package com.marklog.blog.web.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.marklog.blog.domain.post.Post;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class PostResponseDto {
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private final LocalDateTime createdDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private final LocalDateTime modifiedDate;
	private final String title;
	private final String content;

	public PostResponseDto(Post entity) {
		this.createdDate = entity.getCreatedDate();
		this.modifiedDate = entity.getModifiedDate();
		this.title = entity.getTitle();
		this.content = entity.getContent();
	}

}
