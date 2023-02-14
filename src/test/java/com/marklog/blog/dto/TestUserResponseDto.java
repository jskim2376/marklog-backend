package com.marklog.blog.dto;

import java.time.LocalDateTime;

import com.marklog.blog.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TestUserResponseDto {
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;
	private String email;
	private String name;
	private String picture;
	private String title;
	private String introduce;

	public TestUserResponseDto(User entity) {
		this.createdDate = entity.getCreatedDate();
		this.modifiedDate = entity.getModifiedDate();
		this.email = entity.getEmail();
		this.name = entity.getName();
		this.picture = entity.getPicture();
		this.title = entity.getTitle();
		this.introduce = entity.getIntroduce();
	}
}
