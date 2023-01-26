package com.marklog.blog.web;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.marklog.blog.config.auth.LoginUser;
import com.marklog.blog.config.auth.dto.SessionUser;
import com.marklog.blog.service.UserService;
import com.marklog.blog.web.dto.UserResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	
	@GetMapping("/v1/logincheck")
	public SessionUser logincheck(HttpSession session, @LoginUser SessionUser user) {
		if(user!=null) {
			return user;
		}else {
			return null;
		}
	}
	
	@GetMapping("/v1/user/{id}")
	public UserResponseDto user(@PathVariable Long id) {
		
		return userService.findById(id);
	}
}
