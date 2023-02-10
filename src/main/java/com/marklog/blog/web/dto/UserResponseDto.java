package com.marklog.blog.web.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import com.marklog.blog.domain.user.Users;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;
	private String email;
	private String name;
	private String picture;
	private String title;
	private String introduce;

	@Builder
	public UserResponseDto(Users entity) {
		this.createdDate = entity.getCreatedDate();
		this.modifiedDate = entity.getModifiedDate();
		this.email = entity.getEmail();
		this.name = entity.getName();
		this.picture = entity.getPicture();
		this.title = entity.getTitle();
		this.introduce = entity.getIntroduce();
	}

	public UserResponseDto(LocalDateTime createdDate, LocalDateTime modifiedDate, String email, String name,
			String picture, String title, String introduce) {
		super();
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.email = email;
		this.name = name;
		this.picture = picture;
		this.title = title;
		this.introduce = introduce;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdDate, email, introduce, modifiedDate, name, picture, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserResponseDto other = (UserResponseDto) obj;
		return Objects.equals(createdDate, other.createdDate) && Objects.equals(email, other.email)
				&& Objects.equals(introduce, other.introduce) && Objects.equals(modifiedDate, other.modifiedDate)
				&& Objects.equals(name, other.name) && Objects.equals(picture, other.picture)
				&& Objects.equals(title, other.title);
	}


}
