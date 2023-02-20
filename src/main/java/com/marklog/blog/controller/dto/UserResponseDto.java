package com.marklog.blog.controller.dto;

import java.time.LocalDateTime;

import com.marklog.blog.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;
	private String email;
	private String name;
	private String picture;
	private String title;
	private String introduce;

	public UserResponseDto(User entity) {
		this.createdDate = entity.getCreatedDate();
		this.modifiedDate = entity.getModifiedDate();
		this.email = entity.getEmail();
		this.name = entity.getName();
		this.picture = entity.getPicture();
		this.title = entity.getTitle();
		this.introduce = entity.getIntroduce();
	}

	public static UserResponseDto toDto(final User user) {
		return new UserResponseDto(user);
	}
}
