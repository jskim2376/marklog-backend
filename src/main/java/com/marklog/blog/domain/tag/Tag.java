package com.marklog.blog.domain.tag;

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

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.comment.PostComment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50, nullable = false)
	private String name;
	
	@ManyToOne
	@JoinColumn(name = "POST_ID")
	private Post post;
	
	@Builder
	public Tag(Post post, String name) {
		this.post = post;
		this.name = name;
	}
	
	
}
