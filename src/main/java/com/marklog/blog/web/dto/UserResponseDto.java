package com.marklog.blog.web.dto;

import java.sql.Date;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.marklog.blog.domain.post.Post;
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
	
	public UserResponseDto(Users entity) {
		super();
		this.createdDate = entity.getCreatedDate();
		this.modifiedDate = entity.getModifiedDate();
		this.email = entity.getEmail();
		this.name = entity.getName();
		this.picture = entity.getPicture();
	}
	
}
