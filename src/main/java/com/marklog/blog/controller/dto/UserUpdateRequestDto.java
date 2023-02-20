package com.marklog.blog.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDto {
	private String name;
	private String picture;
	private String title;
	private  String introduce;
}
