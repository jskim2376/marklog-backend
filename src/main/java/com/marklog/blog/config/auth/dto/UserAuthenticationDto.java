package com.marklog.blog.config.auth.dto;

import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class UserAuthenticationDto {
	private final Long id;
	private final String email;
	private final Role role;

	public UserAuthenticationDto(User user) {
		super();
		this.id = user.getId();
		this.email = user.getEmail();
		this.role = user.getRole();
	}
}