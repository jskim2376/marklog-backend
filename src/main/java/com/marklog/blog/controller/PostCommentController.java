package com.marklog.blog.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.EmptyResultDataAccessException;
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
import org.springframework.web.bind.annotation.RestController;

import com.marklog.blog.config.auth.dto.UserAuthenticationDto;
import com.marklog.blog.dto.PostCommentResponseDto;
import com.marklog.blog.dto.PostCommentSaveRequestDto;
import com.marklog.blog.dto.PostCommentUpdateRequestDto;
import com.marklog.blog.service.PostCommentService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/v1/post")
@RestController
public class PostCommentController {
	private final PostCommentService postCommentService;

	@GetMapping("/{postId}/comment")
	public List<PostCommentResponseDto> getAllPostCommentByPostId(@PathVariable Long postId) {
		List<PostCommentResponseDto> result = postCommentService.findAllByPostId(postId);
		return result;
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{postId}/comment")
	public ResponseEntity<?> postPostComment(@PathVariable Long postId, @RequestBody PostCommentSaveRequestDto requestDto,
			@AuthenticationPrincipal UserAuthenticationDto userAuthenticationDto) {
		Long userId = userAuthenticationDto.getId();
		Long commentId = postCommentService.save(postId, userId, requestDto);

		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.LOCATION, "/api/v1/post/" + postId + "/comment/" + commentId);

		return ResponseEntity.status(HttpStatus.CREATED).headers(header).build();
	}

	@GetMapping("/{postId}/comment/{commentId}")
	public ResponseEntity<PostCommentResponseDto> getPostComment(@PathVariable("postId") Long postId,
			@PathVariable("commentId") Long commentId,
			@AuthenticationPrincipal UserAuthenticationDto userAuthenticationDto) {
		try {
			PostCommentResponseDto postComment = postCommentService.findById(commentId);
			return ResponseEntity.ok(postComment);
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasPermission(#commentId, 'postComment',null))")
	@PutMapping("/{postId}/comment/{commentId}")
	public ResponseEntity<?> putPostComment(@PathVariable("postId") Long postId,
			@PathVariable("commentId") Long commentId, @RequestBody PostCommentUpdateRequestDto postCommentRequestDto) {
		try {
			postCommentService.update(commentId, postCommentRequestDto);
			HttpHeaders header = new HttpHeaders();
			header.add(HttpHeaders.LOCATION, "/api/v1/post/" + postId + "/comment/" + commentId);
			return ResponseEntity.noContent().headers(header).build();
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasPermission(#commentId, 'postComment',null))")
	@DeleteMapping("/{postId}/comment/{commentId}")
	public ResponseEntity<?> deletePostComment(@PathVariable("postId") Long postId,
			@PathVariable("commentId") Long commentId) {
		try {
			postCommentService.delete(commentId);
			return ResponseEntity.noContent().build();
		} catch (EmptyResultDataAccessException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
