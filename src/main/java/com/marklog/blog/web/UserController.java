package com.marklog.blog.web;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.ResponseStatus;
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
	private final HttpSession session;

	@GetMapping("/user/{id}")
	public ResponseEntity<UserResponseDto> userGet(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(userService.findById(id));
		}
		catch(IllegalArgumentException e){
			return ResponseEntity.badRequest().body(null);
		}
	}

	@PutMapping("/user/{id}")
	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or principal.id==#id)")
	public UserResponseDto userPut(@PathVariable Long id, @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
		return userService.update(id, userUpdateRequestDto);
	}

	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or principal.id==#id)")
	@DeleteMapping("/user/{id}")
	public void userDelete(@PathVariable Long id) {
		SecurityContextHolder.clearContext();
		session.invalidate();
		userService.delete(id);
	}
}
