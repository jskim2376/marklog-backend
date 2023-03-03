package com.marklog.blog.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.postlike.PostLike;
import com.marklog.blog.domain.postlike.PostLikeIdClass;
import com.marklog.blog.domain.postlike.PostLikeRepository;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostLikeService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final PostLikeRepository postLikeRepository;

	@Transactional
	public void save(Long postId, Long userId) {
		Post post = postRepository.getReferenceById(postId);
		User user = userRepository.getReferenceById(userId);
		postLikeRepository.save(new PostLike(post, user));
	}

	@Transactional
	public Boolean findById(Long postId, Long userId) {
		Optional<PostLike> optional = postLikeRepository.findById(new PostLikeIdClass(postId, userId));
		return optional.isPresent();
	}
	
	@Transactional
	public void delete(Long postId, Long userId) {
		PostLike postLike = postLikeRepository.findById(new PostLikeIdClass(postId, userId)).orElseThrow(()->new IllegalArgumentException("존재하지않는 post id입니다="+postId));
		postLikeRepository.delete(postLike);
	}
}
