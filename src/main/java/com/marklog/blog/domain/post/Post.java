package com.marklog.blog.domain.post;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.catalina.User;

import com.marklog.blog.domain.BaseTimeEntity;
import com.marklog.blog.domain.post.comment.PostComment;
import com.marklog.blog.domain.tag.Tag;
import com.marklog.blog.domain.user.Users;

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
	
	@Column(length=50, nullable = false)
	private String title;
	
	@Column(columnDefinition="TEXT", nullable = false)
	private String content;

	@OneToMany(mappedBy = "post")
	private List<PostComment> postComments = new ArrayList<>();
	
	@ManyToOne
	private Users user;
	
	@OneToMany(mappedBy = "post")
	private List<Tag> tags = new ArrayList<>();
	
	@Builder
	public Post(String title, String content, Users user) {
		this.title = title;
		this.content = content;
		this.user = user;
		this.user.getPosts().add(this);
	}
	
	public void update(String title, String content) {
		this.title=title;
		this.content=content;
	}
}
