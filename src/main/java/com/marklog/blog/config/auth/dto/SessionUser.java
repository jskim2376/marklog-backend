package com.marklog.blog.config.auth.dto;
import java.io.Serializable;

import com.marklog.blog.domain.user.Users;

import lombok.Getter;

@Getter
public class SessionUser implements Serializable{
	private Long id;
	private String name;
	private String email;
	private String picture;

	public SessionUser(Users user) {
		this.id = user.getId();
		this.name = user.getName();
		this.email = user.getEmail();
		this.picture = user.getPicture();
	}

	public SessionUser(Long id, String name, String email, String picture) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.picture = picture;
	}
}
