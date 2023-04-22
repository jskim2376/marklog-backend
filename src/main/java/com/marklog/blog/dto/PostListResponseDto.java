package com.marklog.blog.dto;

import java.time.LocalDateTime;
import java.util.List;

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
	private Long postId;
	private String thumbnail;
	private String title;
	private String summary;
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdDate;
	private int commentCount;
	private int likeCount;
	private String picture;
	private String userName;
	private Long userId;
	private List<TagNameResponseDto> tagList;

	public PostListResponseDto(Post entity) {
		this.postId = entity.getId();
		this.thumbnail = entity.getThumbnail();
		this.title = entity.getTitle();
		this.summary = entity.getSummary();
		this.createdDate = entity.getCreatedDate();
		this.commentCount = entity.getPostComments().size();
		this.likeCount = entity.getPostLikes().size();
		this.userName = entity.getUser().getName();
		this.userId = entity.getUser().getId();
	}


}
