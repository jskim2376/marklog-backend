package com.marklog.blog.dto;

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
	private String picture;
	private String userName;
	private Long postCommentId;
	private String content;
	private List<PostCommentResponseDto> childList;

	public static PostCommentResponseDto toDto(PostComment postComment) {
		List<PostCommentResponseDto> postChildCommentResponseDtos = postComment.getChildList().stream()
				.map(PostCommentResponseDto::toDto).collect(Collectors.toList());
		PostCommentResponseDto postCommentResponseDto = new PostCommentResponseDto(postComment.getUser().getId(),postComment.getUser().getPicture(),
				postComment.getUser().getName(), postComment.getId(),postComment.getContent(), postChildCommentResponseDtos);
		return postCommentResponseDto;

	}
}
