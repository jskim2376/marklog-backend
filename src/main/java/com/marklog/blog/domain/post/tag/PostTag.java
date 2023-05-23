package com.marklog.blog.domain.post.tag;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.marklog.blog.domain.BaseTimeEntity;
import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.tag.Tag;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class PostTag extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "POST_ID")
	private Post post;

	@ManyToOne(optional = false)
	@JoinColumn(name = "TAG_ID")
	private Tag tag;

	@Builder
	public PostTag(Post post, Tag tag) {
		this.post = post;
		this.tag = tag;
	}
}
