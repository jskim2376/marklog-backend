package com.marklog.blog.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.marklog.blog.service.PostService;
import com.marklog.blog.web.dto.PostResponseDto;
import com.marklog.blog.web.dto.PostSaveRequestDto;
import com.marklog.blog.web.dto.PostUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class PostAppiController {
	private final PostService postService;

	@PostMapping("/v1/post")
	public Long save(@RequestBody PostSaveRequestDto requestDto) {
		return postService.save(requestDto);
	}
	
	@PutMapping("/v1/post/{id}")
	public Long update(@PathVariable Long id, @RequestBody PostUpdateRequestDto requestDto) {
		return postService.update(id, requestDto);
	}
	
	@GetMapping("/v1/post/{id}")
	public PostResponseDto findById(@PathVariable Long id) {
		return postService.findById(id);
	}
}
