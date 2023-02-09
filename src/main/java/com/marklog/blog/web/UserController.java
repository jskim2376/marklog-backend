package com.marklog.blog.web;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.marklog.blog.config.auth.LoginUser;
import com.marklog.blog.config.auth.dto.SessionUser;
import com.marklog.blog.domain.user.Users;
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
	
	@GetMapping("/logincheck")
	public ResponseEntity test(@LoginUser SessionUser user) {
	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	    if (principal instanceof DefaultOAuth2User && user != null) {
	        return  ResponseEntity.ok(null);
	    }
	    else {
	        return  ResponseEntity.badRequest().build();
	    }
	}

	
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
	@PreAuthorize("isAuthenticated() and (#id == #user.getId() or hasRole('ADMIN'))")
	public Long userPut(@PathVariable Long id, @RequestBody UserUpdateRequestDto userUpdateRequestDto, @LoginUser SessionUser user) {
		return userService.update(id, userUpdateRequestDto);
	}
	
	@DeleteMapping("/user/{id}")
	@PreAuthorize("isAuthenticated() and (#id == #user.getId() or hasRole('ADMIN'))")
	public void userDelete(@PathVariable Long id, @LoginUser SessionUser user) {
		SecurityContextHolder.clearContext();
		session.invalidate();
		userService.delete(id);
	}
}
