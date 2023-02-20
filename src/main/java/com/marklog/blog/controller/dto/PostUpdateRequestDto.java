package com.marklog.blog.controller.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateRequestDto {
	private String title;
	private String content;
	private List<String> tagList;
}
