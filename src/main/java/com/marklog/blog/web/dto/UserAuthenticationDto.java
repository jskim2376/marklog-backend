package com.marklog.blog.web.dto;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.Users;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class UserAuthenticationDto {
	private Long id;
	private String email;
	private Role role;
	
	public UserAuthenticationDto(Users user) {
		super();
		this.id = user.getId();
		this.email = user.getEmail();
		this.role = user.getRole();
	}
}