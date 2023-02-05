package com.marklog.blog.domain.postlike;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.user.Users;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@IdClass(PostLikeId.class)
public class PostLike {
	@Id
	@ManyToOne
	@JoinColumn(name = "POST_ID")
	Post post;

	@Id
	@ManyToOne
	@JoinColumn(name = "USERS_ID")
	Users user;
	
}
