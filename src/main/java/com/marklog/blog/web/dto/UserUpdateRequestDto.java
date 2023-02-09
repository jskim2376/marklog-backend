package com.marklog.blog.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequestDto {
	private String email;
	private String name;
	private String picture;
	private String title;
	private String introduce;

	@Builder
	public UserUpdateRequestDto(String email, String name, String picture, String title, String introduce) {
		this.email = email;
		this.name = name;
		this.picture = picture;
		this.title = title;
		this.introduce = introduce;
	}
	
}
