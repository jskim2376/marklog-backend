package com.marklog.blog.controller.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.marklog.blog.domain.post.comment.PostComment;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentResponseDto {
	private Long userId;
	private String userName;
	private String content;
	private List<PostCommentResponseDto> childList;

	public static PostCommentResponseDto toDto(PostComment postComment) {
		List<PostCommentResponseDto> postChildCommentResponseDtos = postComment.getChildList().stream()
				.map(PostCommentResponseDto::toDto).collect(Collectors.toList());
		PostCommentResponseDto postCommentResponseDto = new PostCommentResponseDto(postComment.getUser().getId(),
				postComment.getUser().getName(), postComment.getContent(), postChildCommentResponseDtos);
		return postCommentResponseDto;

	}
}
