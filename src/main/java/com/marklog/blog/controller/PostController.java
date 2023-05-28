package com.marklog.blog.controller;

import java.util.NoSuchElementException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marklog.blog.config.auth.dto.UserAuthenticationDto;
import com.marklog.blog.dto.PostListResponseDto;
import com.marklog.blog.dto.PostResponseDto;
import com.marklog.blog.dto.PostSaveRequestDto;
import com.marklog.blog.dto.PostUpdateRequestDto;
import com.marklog.blog.service.PostLikeService;
import com.marklog.blog.service.PostService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/v1/post")
@RestController
public class PostController {
	private final PostService postService;
	private final PostLikeService postLikeService;

	@GetMapping
	public ResponseEntity<Page<PostListResponseDto>> recent(Pageable pageable) {
		try {
			Page<PostListResponseDto> result = postService.recentPost(pageable);
			return ResponseEntity.ok(result);
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping
	public ResponseEntity<?> postPostByUserId(@RequestBody PostSaveRequestDto requestDto,
			@AuthenticationPrincipal UserAuthenticationDto userAuthenticationDto) {
		Long id = postService.save(userAuthenticationDto.getId(), requestDto);
		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.LOCATION, "/api/v1/post/" + id);
		return ResponseEntity.status(HttpStatus.CREATED).headers(header).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long id,
			@AuthenticationPrincipal UserAuthenticationDto userAuthenticationDto) {
		try {
			PostResponseDto postResponseDto = postService.findById(id);
			if (userAuthenticationDto != null) {
				Boolean like = postLikeService.findById(id, userAuthenticationDto.getId());
				postResponseDto.setLike(like);
			}

			return ResponseEntity.ok(postResponseDto);
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasPermission(#id, 'post',null))")
	@PutMapping("/{id}")
	public ResponseEntity<?> putPostById(@PathVariable Long id, @RequestBody PostUpdateRequestDto requestDto) {
		try {
			postService.update(id, requestDto);
			HttpHeaders header = new HttpHeaders();
			header.add(HttpHeaders.LOCATION, "/api/v1/post/" + id);
			return ResponseEntity.noContent().headers(header).build();
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}

	}

	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasPermission(#id, 'post',null))")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePostById(@PathVariable Long id) {
		try {
			postService.delete(id);
			return ResponseEntity.noContent().build();
		} catch (EmptyResultDataAccessException e) {
			return ResponseEntity.notFound().build();
		}
	}

}
