package com.marklog.blog.domain.post;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.marklog.blog.domain.BaseTimeEntity;
import com.marklog.blog.domain.post.comment.PostComment;
import com.marklog.blog.domain.tag.Tag;
import com.marklog.blog.domain.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Post extends BaseTimeEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length=100)
	private String thumbnail;
	
	@Column(length=30)
	private String summary;

	@Column(length=50, nullable = false)
	private String title;

	
	@Column(columnDefinition="TEXT", nullable = false)
	private String content;

	@ManyToOne(optional = false)
	private User user;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	private List<Tag> tags = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	private List<PostComment> postComments = new ArrayList<>();

	public Post(String thubnail, String summary, String title, String content, User user, List<Tag> tags) {
		this.thumbnail = thubnail;
		this.summary = summary;
		this.title = title;
		this.content = content;
		this.user = user;
		this.user.getPosts().add(this);
		this.tags = tags;
	}

	public void update(String title, String content) {
		this.title=title;
		this.content=content;
	}
}
