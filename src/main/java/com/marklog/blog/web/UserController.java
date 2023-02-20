package com.marklog.blog.web;



import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marklog.blog.service.UserService;
import com.marklog.blog.web.dto.UserResponseDto;
import com.marklog.blog.web.dto.UserUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class UserController {
	private final UserService userService;

	@PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
	@GetMapping("/user")
	public Page<UserResponseDto> getAllUsers(Pageable pageable) {
	    return userService.findAll(pageable);
	}

	@GetMapping("/user/{id}")
	public ResponseEntity<UserResponseDto> userGet(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(userService.findById(id));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}

	}

	@PutMapping("/user/{id}")
	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or principal.id==#id)")
	public ResponseEntity userPut(@PathVariable Long id, @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.LOCATION, "/api/v1/user/"+id);
		try {
			userService.update(id, userUpdateRequestDto);
			return new ResponseEntity(header, HttpStatus.NO_CONTENT);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or principal.id==#id)")
	@DeleteMapping("/user/{id}")
	public ResponseEntity userDelete(@PathVariable Long id) {
		try {
			SecurityContextHolder.clearContext();
			userService.delete(id);
			return ResponseEntity.noContent().build();
		} catch (EmptyResultDataAccessException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
