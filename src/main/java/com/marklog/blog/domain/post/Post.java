package com.marklog.blog.domain.post;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.marklog.blog.domain.BaseTimeEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Post extends BaseTimeEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=500, nullable = false)
	private String title;
	
	@Column(columnDefinition="TEXT", nullable = false)
	private String content;

	@Builder
	public Post(String title, String content) {
		this.title = title;
		this.content = content;
	}
	
	public void update(String title, String content) {
		this.title=title;
		this.content=content;
	}
}
