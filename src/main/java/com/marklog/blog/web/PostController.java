package com.marklog.blog.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import com.marklog.blog.domain.user.Role;
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
	public ResponseEntity<PostIdResponseDto> save(@RequestBody PostSaveRequestDto requestDto) {
		Long id = postService.save(requestDto);
		PostIdResponseDto postIdResponseDto = new PostIdResponseDto(id);
		return new ResponseEntity<>(postIdResponseDto, HttpStatus.CREATED);
	}

	@GetMapping("/post/{id}")
	public ResponseEntity<PostResponseDto> findById(@PathVariable Long id) {
		try {
			return new ResponseEntity<>(postService.findById(id), HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PreAuthorize("isAuthenticated()")
//	@PostAuthorize("hasRole('ADMIN') or #userId==principal.id")
	@PutMapping("/post/{id}")
	public ResponseEntity<PostIdResponseDto> update(@PathVariable Long id, @RequestBody PostUpdateRequestDto requestDto,
			Authentication authentication) {
		try {
			PostResponseDto postResponseDto = postService.findById(id);
			Long updatedId = postService.update(id, requestDto);
			PostIdResponseDto postIdResponseDto = new PostIdResponseDto(updatedId);
			Long userId = postResponseDto.getUserId();
			Long authUserId = ((UserAuthenticationDto) authentication.getPrincipal()).getId();
			boolean hasAdmin = authentication.getAuthorities()
					.contains(new SimpleGrantedAuthority(Role.ADMIN.getKey()));

			if (hasAdmin || userId == authUserId) {
				return new ResponseEntity<>(postIdResponseDto, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@PreAuthorize("isAuthenticated()")
//	@PostAuthorize("hasRole('ADMIN') or #userId==#authUserId")
	@DeleteMapping("/post/{id}")
	public ResponseEntity userDelete(@PathVariable Long id, Authentication authentication) {
		try {
			PostResponseDto postResponseDto = postService.findById(id);
			Long userId = postResponseDto.getUserId();
			Long authUserId = ((UserAuthenticationDto) authentication.getPrincipal()).getId();
			boolean hasAdmin = authentication.getAuthorities()
					.contains(new SimpleGrantedAuthority(Role.ADMIN.getKey()));

			if (hasAdmin || userId == authUserId) {
				postService.delete(id);
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			} else {
				return new ResponseEntity<PostIdResponseDto>(HttpStatus.FORBIDDEN);
			}
		} catch (IllegalArgumentException e) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
	}
}
