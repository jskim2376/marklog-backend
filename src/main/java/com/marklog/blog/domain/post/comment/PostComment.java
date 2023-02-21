package com.marklog.blog.domain.post.comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.marklog.blog.domain.BaseTimeEntity;
import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.user.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class PostComment extends BaseTimeEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(columnDefinition="TEXT", nullable = false)
	private String content;

	@ManyToOne
	@JoinColumn(name = "POST_ID")
	private Post post;

	@ManyToOne
	@JoinColumn(name = "USER_ID")
	private User user;

	@Builder
	public PostComment(Post post, User user, String content) {
		this.content = content;
		this.post = post;
	}
	
	public void update(String content) {
		this.content=content;
	}
}
