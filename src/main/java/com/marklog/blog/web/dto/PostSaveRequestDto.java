package com.marklog.blog.web.dto;

import java.util.List;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.user.Users;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostSaveRequestDto {
	private String title;
	private String content;
	private Long userId;
	private List<String> tags;
	
	@Builder
	public PostSaveRequestDto(String title, String content, Long userId, List<String> tags) {
		this.title = title;
		this.content = content;
		this.userId = userId;
		this.tags = tags;
	}
	
	
}
