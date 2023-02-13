package com.marklog.blog.web;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.marklog.blog.service.PostService;
import com.marklog.blog.web.dto.PostIdResponseDto;
import com.marklog.blog.web.dto.PostResponseDto;
import com.marklog.blog.web.dto.PostSaveRequestDto;
import com.marklog.blog.web.dto.PostUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class PostController {
	private final PostService postService;

	@ResponseStatus(value = HttpStatus.CREATED)
	@PreAuthorize("isAuthenticated()")
@PostMapping("/post")
	public PostIdResponseDto save(@RequestBody PostSaveRequestDto requestDto) {
		Long id = postService.save(requestDto);
		PostIdResponseDto postIdResponseDto = new PostIdResponseDto(id);
		return postIdResponseDto;
	}


	@GetMapping("/post/{id}")
	public PostResponseDto findById(@PathVariable Long id) {
		return postService.findById(id);
	}

	@PreAuthorize("isAuthenticated()")
	@PutMapping("/post/{id}")
	public PostIdResponseDto update(@PathVariable Long id, @RequestBody PostUpdateRequestDto requestDto) {
		Long updatedId = postService.update(id, requestDto);
		PostIdResponseDto postIdResponseDto = new PostIdResponseDto(updatedId);
		return postIdResponseDto;
	}

	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/post/{id}")
	public void userDelete(@PathVariable Long id) {
		postService.delete(id);
	}
}
