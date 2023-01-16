package com.marklog.blog.web.dto;

import com.marklog.blog.domain.post.Post;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostSaveRequestDto {
	private String title;
	private String content;

	@Builder
	public PostSaveRequestDto(String title, String content) {
		this.title = title;
		this.content = content;
	}
	
	public Post toEntity() {
		return Post.builder()
				.title(title)
				.content(content)
				.build();
	}
	
}
