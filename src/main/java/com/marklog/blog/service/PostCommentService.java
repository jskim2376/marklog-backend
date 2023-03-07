package com.marklog.blog.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marklog.blog.controller.dto.PostCommentRequestDto;
import com.marklog.blog.controller.dto.PostCommentResponseDto;
import com.marklog.blog.controller.dto.PostCommentUpdateRequestDto;
import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.post.comment.PostComment;
import com.marklog.blog.domain.post.comment.PostCommentRepository;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class PostCommentService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final PostCommentRepository postCommentRepository;
	private final NoticeService noticeService;

	public Long save(Long postId, Long userId, PostCommentRequestDto requestDto) {
		Post post = postRepository.getReferenceById(postId);
		User user = userRepository.getReferenceById(userId);

		PostComment postComment;
		if (requestDto.getParentComment() == null) {
			postComment = new PostComment(post, user, requestDto.getContent());
		} else {
			postComment = new PostComment(post, user, requestDto.getContent());
			PostComment parentPostComment = postCommentRepository.getReferenceById(requestDto.getParentComment());
			postComment.setParent(parentPostComment);
		}
		postComment = postCommentRepository.save(postComment);
		
		noticeService.pushNoticeByUserId("\'"+post.getTitle()+"\'" + "에 새로운 댓글이 추가 되었습니다.", userId);
		return postComment.getId();
	}

	public List<PostCommentResponseDto> findAllByPostId(Long postId) {
		Post post = postRepository.getReferenceById(postId);
		List<PostComment> comments = postCommentRepository.findAllByPostAndParentIsNull(post);
		List<PostCommentResponseDto> postCommentResponseDtos = comments.stream().map(PostCommentResponseDto::toDto)
				.collect(Collectors.toList());
		return postCommentResponseDtos;
	}

	public PostCommentResponseDto findById(Long id) {
		PostComment postComment = postCommentRepository.findById(id).orElseThrow();
		PostCommentResponseDto postCommentResponseDto = PostCommentResponseDto.toDto(postComment);
		return postCommentResponseDto;
	}

	public void update(Long id, PostCommentUpdateRequestDto requestDto) {
		PostComment postComment = postCommentRepository.findById(id).orElseThrow();
		postComment.update(requestDto.getContent());
	}

	public void delete(Long id) {
		postCommentRepository.deleteById(id);
	}
}
