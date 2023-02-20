package com.marklog.blog.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.postlike.PostLike;
import com.marklog.blog.domain.postlike.PostLikeIdClass;
import com.marklog.blog.domain.postlike.PostLikeRepository;
import com.marklog.blog.domain.tag.Tag;
import com.marklog.blog.domain.tag.TagRepository;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;
import com.marklog.blog.web.dto.PostResponseDto;
import com.marklog.blog.web.dto.PostSaveRequestDto;
import com.marklog.blog.web.dto.PostUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final TagRepository tagRepository;
	private final PostLikeRepository postLikeRepository;

	@Transactional
	public Long save(Long userId, PostSaveRequestDto requestDto) {
		User user = userRepository.getReferenceById(userId);
		List<String> tagNames = requestDto.getTagList();

		Post post = Post.builder().title(requestDto.getTitle()).content(requestDto.getContent()).user(user).build();
		post = postRepository.save(post);
		if(tagNames != null) {
			for(String tagName: tagNames) {
				Tag tag = Tag.builder().name(tagName).post(post).build();
				tagRepository.save(tag);
			}
		}
		return post.getId();
	}

	public PostResponseDto findById(Long id) {
		Post entity = postRepository.findById(id).orElseThrow(()->new IllegalArgumentException("존재하지않는 id입니다="+id));
		return new PostResponseDto(entity);
	}

	public Page<PostResponseDto> findAll(Pageable pageable) {
		Page<PostResponseDto> pageUserResponseDto =  postRepository.findAll(pageable).map(PostResponseDto::toDto);
		return pageUserResponseDto;
	}

	@Transactional
	public void update(Long id, PostUpdateRequestDto requestDto) {
		Post post = postRepository.findById(id).orElseThrow(()->new IllegalArgumentException("존재하지않는 id입니다="+id));
		post.update(requestDto.getTitle(), requestDto.getContent());

		List<Tag> tags = tagRepository.findByPost(post);
		for(Tag tag : tags) {
			tagRepository.delete(tag);
		}
		List<String> tagNames = requestDto.getTagList();
		if(tagNames != null) {
			for(String tagName: tagNames) {
				tagRepository.save(Tag.builder().name(tagName).post(post).build());
			}
		}
	}

	public void delete(Long id) {
		postRepository.deleteById(id);
	}

	@Transactional
	public void postLikeSave(Long postId, Long userId) {
		Post post = postRepository.getReferenceById(postId);
		User user = userRepository.getReferenceById(userId);
		postLikeRepository.save(new PostLike(post, user));
	}
	
	@Transactional
	public Boolean postLikeFindById(Long postId, Long userId) {
		Optional<PostLike> optional = postLikeRepository.findById(new PostLikeIdClass(postId, userId));
		return optional.isPresent();
	}

	@Transactional
	public void postLikeDelete(Long postId, Long userId) {
		PostLike postLike = postLikeRepository.findById(new PostLikeIdClass(postId, userId)).orElseThrow(()->new IllegalArgumentException("존재하지않는 post id입니다="+postId));
		postLikeRepository.delete(postLike);
	}

}
