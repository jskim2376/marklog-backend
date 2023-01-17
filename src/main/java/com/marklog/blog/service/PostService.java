package com.marklog.blog.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.web.dto.PostResponseDto;
import com.marklog.blog.web.dto.PostSaveRequestDto;
import com.marklog.blog.web.dto.PostUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostService {
	private final PostRepository postRepository;
	
	@Transactional
	public Long save(PostSaveRequestDto requestDto) {
		return postRepository.save(requestDto.toEntity()).getId();
	}
	
	@Transactional
	public Long update(Long id, PostUpdateRequestDto requestDto) {
		Post post = postRepository.findById(id).orElseThrow(()->new IllegalArgumentException("존재하지않는 id입니다="+id));
		post.update(requestDto.getTitle(), requestDto.getContent());
		return id;
	}
	
	public PostResponseDto findById(Long id) {
		Post entity = postRepository.findById(id).orElseThrow(()->new IllegalArgumentException("존재하지않는 id입니다="+id));
		return new PostResponseDto(entity);
	}
}
