package com.marklog.blog.controller;

import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.marklog.blog.config.auth.dto.UserAuthenticationDto;
import com.marklog.blog.service.PostLikeService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/v1/post")
@Controller
public class PostLikeController {
	private final PostLikeService postLikeService;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{id}/like")
	public ResponseEntity<?> postPostLike(@PathVariable Long id,
			@AuthenticationPrincipal UserAuthenticationDto userAuthenticationDto) {
		try {
			postLikeService.save(id, userAuthenticationDto.getId());
			return ResponseEntity.created(null).build();
		} catch (JpaObjectRetrievalFailureException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{id}/like")
	public ResponseEntity<?> deletePostLike(@PathVariable Long id,
			@AuthenticationPrincipal UserAuthenticationDto userAuthenticationDto) {
		try {
			postLikeService.delete(id, userAuthenticationDto.getId());
			return ResponseEntity.noContent().build();
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}

}
