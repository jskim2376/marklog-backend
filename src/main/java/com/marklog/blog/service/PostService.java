package com.marklog.blog.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marklog.blog.controller.dto.PostResponseDto;
import com.marklog.blog.controller.dto.PostSaveRequestDto;
import com.marklog.blog.controller.dto.PostUpdateRequestDto;
import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.tag.Tag;
import com.marklog.blog.domain.tag.TagRepository;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final TagRepository tagRepository;

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
		Post entity = postRepository.findById(id).orElseThrow();
		return new PostResponseDto(entity);
	}

	public Page<PostResponseDto> findAll(Pageable pageable) {
		Page<PostResponseDto> pageUserResponseDto =  postRepository.findAll(pageable).map(PostResponseDto::toDto);
		return pageUserResponseDto;
	}

	public void update(Long id, PostUpdateRequestDto requestDto) {
		Post post = postRepository.findById(id).orElseThrow();
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

}
