package com.marklog.blog.web.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDto {
	private String name;
	private String picture;
	private String title;
	private  String introduce;
}
