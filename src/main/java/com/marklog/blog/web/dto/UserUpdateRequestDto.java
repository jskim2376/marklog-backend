package com.marklog.blog.web.dto;

import java.util.Objects;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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

	@Override
	public int hashCode() {
		return Objects.hash(introduce, name, picture, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		UserUpdateRequestDto other = (UserUpdateRequestDto) obj;
		return Objects.equals(introduce, other.introduce) && Objects.equals(name, other.name)
				&& Objects.equals(picture, other.picture) && Objects.equals(title, other.title);
	}



}
