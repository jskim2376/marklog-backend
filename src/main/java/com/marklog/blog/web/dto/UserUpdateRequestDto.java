package com.marklog.blog.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequestDto {
	private String name;
	private String picture;
	
	@Builder
	public UserUpdateRequestDto(String name, String picture) {
		super();
		this.name = name;
		this.picture = picture;
	}	
}
