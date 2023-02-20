package com.marklog.blog.web;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
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
import com.marklog.blog.service.PostService;
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
	public ResponseEntity<Object> save(@RequestBody PostSaveRequestDto requestDto,
			@AuthenticationPrincipal UserAuthenticationDto userAuthenticationDto) {
		Long id = postService.save(userAuthenticationDto.getId(), requestDto);
		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.LOCATION, "/api/v1/post/" + id);
		return new ResponseEntity<>(header, HttpStatus.CREATED);
	}

	@GetMapping("/post")
	public Page<PostResponseDto> getAllUsers(Pageable pageable) {
		return postService.findAll(pageable);
	}

	@GetMapping("/post/{id}")
	public ResponseEntity<PostResponseDto> findById(@PathVariable Long id,
			@AuthenticationPrincipal UserAuthenticationDto userAuthenticationDto) {
		try {
			PostResponseDto postResponseDto = postService.findById(id);
			if (userAuthenticationDto != null) {
				Boolean like = postService.postLikeFindById(id, userAuthenticationDto.getId());
				postResponseDto.setLike(like);
			}

			return new ResponseEntity<>(postResponseDto, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasPermission(#id, 'post',null))")
	@PutMapping("/post/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PostUpdateRequestDto requestDto) {
		try {
			postService.update(id, requestDto);
			HttpHeaders header = new HttpHeaders();
			header.add(HttpHeaders.LOCATION, "/api/v1/post/" + id);
			return new ResponseEntity<>(header, HttpStatus.NO_CONTENT);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasPermission(#id, 'post',null))")
	@DeleteMapping("/post/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		try {
			postService.delete(id);
			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
		} catch (EmptyResultDataAccessException e) {
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		}
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/post/like/{id}")
	public ResponseEntity<?> postLikeSave(@PathVariable Long id,
			@AuthenticationPrincipal UserAuthenticationDto userAuthenticationDto) {
		try {
			postService.postLikeSave(id, userAuthenticationDto.getId());
			return new ResponseEntity<Object>(HttpStatus.CREATED);
		} catch (JpaObjectRetrievalFailureException e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		}
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/post/like/{id}")
	public ResponseEntity<?> postLikeDelete(@PathVariable Long id,
			@AuthenticationPrincipal UserAuthenticationDto userAuthenticationDto) {
		try {
			postService.postLikeDelete(id, userAuthenticationDto.getId());
			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		}
	}

}
