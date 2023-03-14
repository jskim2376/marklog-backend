package com.marklog.blog.dto;

import java.util.ArrayList;
import java.util.List;

import com.marklog.blog.domain.tag.Tag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TagNameResponseDto {
	String name;

	public static List<TagNameResponseDto> toEntityDto(List<Tag> tags) {
		List<TagNameResponseDto> tagList = new ArrayList<>();

		for (Tag tag : tags) {
			tagList.add(new TagNameResponseDto(tag.getName()));
		}

		return tagList;
	}
}
