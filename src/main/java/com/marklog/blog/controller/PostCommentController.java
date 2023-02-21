package com.marklog.blog.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.marklog.blog.config.auth.dto.UserAuthenticationDto;
import com.marklog.blog.controller.dto.PostCommentRequestDto;
import com.marklog.blog.domain.post.comment.PostComment;
import com.marklog.blog.service.PostCommentService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class PostCommentController {
	private final PostCommentService postCommentService;

	@GetMapping("/post/{postId}/comment")
	public List<PostComment> getFindAllByPost(@PathVariable Long postId) {
		return postCommentService.findAll(postId);
	}

	@ResponseStatus(value = HttpStatus.CREATED)
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/post/{postId}/comment")
	public ResponseEntity<Object> save(@PathVariable Long postId, @RequestBody PostCommentRequestDto requestDto,
			@AuthenticationPrincipal UserAuthenticationDto userAuthenticationDto) {
		Long userId = userAuthenticationDto.getId();
		Long commentId = postCommentService.save(postId, userId, requestDto);

		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.LOCATION, "/api/v1/post/" + postId + "/comment/" + commentId);

		return new ResponseEntity<>(header, HttpStatus.CREATED);
	}

	@GetMapping("/post/{postId}/comment/{commentId}")
	public ResponseEntity<PostComment> findById(@PathVariable("postId") Long postId,
			@PathVariable("commentId") Long commentId,
			@AuthenticationPrincipal UserAuthenticationDto userAuthenticationDto) {
		try {
			PostComment postComment = postCommentService.findById(commentId);
			return new ResponseEntity<>(postComment, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasPermission(#commentId, 'postComment',null))")
	@PutMapping("/post/{postId}/comment/{commentId}")
	public ResponseEntity<?> update(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId,
			@RequestBody PostCommentRequestDto postCommentRequestDto) {
		try {
			postCommentService.update(commentId, postCommentRequestDto);
			HttpHeaders header = new HttpHeaders();
			header.add(HttpHeaders.LOCATION, "/api/v1/post/" + postId + "/comment/" + commentId);
			return new ResponseEntity<>(header, HttpStatus.NO_CONTENT);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}
	
	
	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasPermission(#commentId, 'postComment',null))")
	@DeleteMapping("/post/{postId}/comment/{commentId}")
	public ResponseEntity<?> delete(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
		try {
			postCommentService.delete(commentId);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (EmptyResultDataAccessException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
