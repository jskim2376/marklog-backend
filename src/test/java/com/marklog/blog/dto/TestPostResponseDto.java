package com.marklog.blog.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.marklog.blog.domain.post.Post;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TestPostResponseDto {
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime modifiedDate;
	private String title;
	private String content;
	private Long userId;
	public TestPostResponseDto(Post entity) {
		this.createdDate = entity.getCreatedDate();
		this.modifiedDate = entity.getModifiedDate();
		this.title = entity.getTitle();
		this.content = entity.getContent();
		this.userId = entity.getUser().getId();
	}

}
