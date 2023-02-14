package com.marklog.blog.web;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
	public ResponseEntity save(@RequestBody PostSaveRequestDto requestDto) {

		Long id = postService.save(requestDto);
		
		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.LOCATION, "/api/v1/post/"+id);
		return new ResponseEntity<>(header, HttpStatus.CREATED);
	}

	@GetMapping("/post/{id}")
	public ResponseEntity<PostResponseDto> findById(@PathVariable Long id) {
		try {
			return new ResponseEntity<>(postService.findById(id), HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasPermission(#id, 'post',null))")
	@PutMapping("/post/{id}")
	public ResponseEntity update(@PathVariable Long id, @RequestBody PostUpdateRequestDto requestDto,
			Authentication authentication) {
		try {
			HttpHeaders header = new HttpHeaders();
			header.add(HttpHeaders.LOCATION, "/api/v1/post/"+id);
			postService.update(id, requestDto);
			return new ResponseEntity<>(header, HttpStatus.NO_CONTENT);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasPermission(#id, 'post',null))")
	@DeleteMapping("/post/{id}")
	public ResponseEntity userDelete(@PathVariable Long id, Authentication authentication) {
		try {
			postService.delete(id);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		} catch (EmptyResultDataAccessException e) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
	}
}
