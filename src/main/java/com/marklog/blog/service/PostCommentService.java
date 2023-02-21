package com.marklog.blog.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.marklog.blog.controller.dto.PostCommentRequestDto;
import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.post.comment.PostComment;
import com.marklog.blog.domain.post.comment.PostCommentRepository;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostCommentService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final PostCommentRepository postCommentRepository;

	@Transactional
	public Long save(Long postId, Long userId, PostCommentRequestDto requestDto) {
		Post post = postRepository.getReferenceById(postId);
		User user = userRepository.getReferenceById(userId);
		PostComment postComment = postCommentRepository.save(new PostComment(post, user, requestDto.getContent()));
		return postComment.getId();
	}
	
	public List<PostComment> findAll(Long postId){
		Post post = postRepository.getReferenceById(postId);
		return postCommentRepository.findAllByPost(post);
	}

	@Transactional
	public PostComment findById(Long id) {
		PostComment postComment = postCommentRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("존재하지않는 post comment id입니다=" + id));
		return postComment;
	}

	@Transactional
	public void update(Long id, PostCommentRequestDto requestDto) {
		PostComment postComment = postCommentRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("존재하지않는 post comment id입니다=" + id));

		postComment.update(requestDto.getContent());
	}


	@Transactional
	public void delete(Long id) {
		PostComment postComment = postCommentRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("존재하지않는 post comment id입니다=" + id));
		postCommentRepository.delete(postComment);
	}
}
