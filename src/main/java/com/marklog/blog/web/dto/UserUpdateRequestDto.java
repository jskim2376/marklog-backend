package com.marklog.blog.web.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class UserUpdateRequestDto {
	private String name;
	private String picture;
	private String title;
	private String introduce;

	@Builder
	public UserUpdateRequestDto(String name, String picture, String title, String introduce) {
		this.name = name;
		this.picture = picture;
		this.title = title;
		this.introduce = introduce;
	}

}
