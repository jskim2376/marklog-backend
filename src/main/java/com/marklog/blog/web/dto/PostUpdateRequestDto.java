package com.marklog.blog.web.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateRequestDto {
	private String title;
	private String content;
	private List<String> tagNames;
}
