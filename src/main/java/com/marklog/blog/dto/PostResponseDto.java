package com.marklog.blog.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.marklog.blog.domain.post.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime modifiedDate;
	private String title;
	private String content;
	private Long userId;
	private String userName;
	private Boolean like;
	private List<TagResponseDto> tagList;

	public PostResponseDto(Post entity) {
		this.createdDate = entity.getCreatedDate();
		this.modifiedDate = entity.getModifiedDate();
		this.title = entity.getTitle();
		this.content = entity.getContent();
		this.userId = entity.getUser().getId();
		this.userName = entity.getUser().getName();
		this.like = false;
		this.tagList = TagResponseDto.toEntityDto(entity.getPostTags());
	}

	public static PostResponseDto toDto(Post entity) {
		return new PostResponseDto(entity);
	}

	public void setLike(Boolean like) {
		this.like = like;
	}
}
