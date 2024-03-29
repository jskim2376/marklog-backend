package com.marklog.blog.controller;

import java.util.NoSuchElementException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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

import com.marklog.blog.dto.UserResponseDto;
import com.marklog.blog.dto.UserUpdateRequestDto;
import com.marklog.blog.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/v1/user")
@RestController
public class UserController {
	private final UserService userService;

	@PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
	@GetMapping
	public Page<UserResponseDto> getAllUsers(Pageable pageable) {
		return userService.findAll(pageable);
	}

	@GetMapping("/logout")
	public void logout(HttpServletResponse response) {
	    Cookie cookie = new Cookie("refresh_token", null);
	    cookie.setMaxAge(0);
	    cookie.setSecure(true);
	    cookie.setHttpOnly(true);
	    cookie.setPath("/api");
	    response.addCookie(cookie);
	    
		response.setHeader("Location", "/");
		response.setStatus(302);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(userService.findById(id));
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}

	}

	@PutMapping("/{id}")
	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or principal.id==#id)")
	public ResponseEntity<?> putUserById(@PathVariable Long id,
			@RequestBody UserUpdateRequestDto userUpdateRequestDto) {
		try {
			userService.update(id, userUpdateRequestDto);
			HttpHeaders header = new HttpHeaders();
			header.add(HttpHeaders.LOCATION, "/api/v1/user/" + id);
			return ResponseEntity.noContent().headers(header).build();
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or principal.id==#id)")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
		try {
			userService.delete(id);
			SecurityContextHolder.clearContext();
			return ResponseEntity.noContent().build();
		} catch (EmptyResultDataAccessException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
