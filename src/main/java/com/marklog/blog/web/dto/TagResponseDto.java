package com.marklog.blog.web.dto;

import java.util.ArrayList;
import java.util.List;

import com.marklog.blog.domain.tag.Tag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TagResponseDto {
	String name;

	public static List<TagResponseDto> toEntityDto(List<Tag> tags) {
		List<TagResponseDto> tagList = new ArrayList<>();

		for(Tag tag: tags) {
			tagList.add(new TagResponseDto(tag.getName()));
		}

		return tagList;
	}
}
