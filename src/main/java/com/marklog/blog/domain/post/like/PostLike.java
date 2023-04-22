package com.marklog.blog.domain.post.like;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@IdClass(PostLikeIdClass.class)
public class PostLike {
	@Id
	@ManyToOne
	@JoinColumn(name = "POST_ID")
	Post post;

	@Id
	@ManyToOne
	@JoinColumn(name = "USERS_ID")
	User user;

	public PostLike(Post post, User user) {
		this.post = post;
		this.user = user;
	}
}
