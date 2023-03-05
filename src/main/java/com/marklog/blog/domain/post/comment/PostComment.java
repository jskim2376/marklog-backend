package com.marklog.blog.domain.post.comment;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.marklog.blog.domain.BaseTimeEntity;
import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.user.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class PostComment extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	@ManyToOne(optional = false)
	@JoinColumn(name = "USER_ID")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "POST_ID")
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ID")
	private PostComment parent;

	@OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade =  CascadeType.REMOVE)
	private List<PostComment> childList = new ArrayList<>();

	@Builder
	public PostComment(Post post, User user, String content) {
		this.content = content;
		this.post = post;
		post.getPostComments().add(this);
		this.user = user;
	}

	public void update(String content) {
		this.content = content;
	}

	public void addChildComment(PostComment childComment) {
		this.childList.add(childComment);
		if (childComment.getParent() != this) {
			childComment.setParent(this);
		}
	}

	public void setParent(PostComment parentComment) {
		this.parent = parentComment;
		if (!parentComment.getChildList().contains(this)) {
			parentComment.addChildComment(this);
		}
	}
}
