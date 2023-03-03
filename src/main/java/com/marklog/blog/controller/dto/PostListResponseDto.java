package com.marklog.blog.controller.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import com.marklog.blog.domain.post.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class PostListResponseDto {
	private String thumnail;
	private String title;
	private String summary;
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime modifiedDate;
	private int commentCount;
	private int likeCount;
	private String picture;
	private Long userId;
	private String userName;

	public PostListResponseDto(Post entity) {
		this.thumnail = entity.getThumbnail();
		this.title = entity.getTitle();
		this.summary = entity.getSummary();
		this.modifiedDate = entity.getModifiedDate();
		this.commentCount = entity.getPostComments().size();
		this.likeCount = entity.getPostLikes().size();
		this.userId = entity.getUser().getId();
		this.userName = entity.getUser().getName();
	}

}
