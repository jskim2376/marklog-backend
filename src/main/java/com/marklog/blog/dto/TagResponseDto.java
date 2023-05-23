package com.marklog.blog.dto;

import java.util.ArrayList;
import java.util.List;

import com.marklog.blog.domain.post.tag.PostTag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TagResponseDto {
	String name;

	public static List<TagResponseDto> toEntityDto(List<PostTag> postTags) {
		List<TagResponseDto> tagList = new ArrayList<>();

		for (PostTag postTag : postTags) {
			tagList.add(new TagResponseDto(postTag.getTag().getName()));
		}

		return tagList;
	}
}
