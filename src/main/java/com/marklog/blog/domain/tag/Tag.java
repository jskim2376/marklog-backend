package com.marklog.blog.domain.tag;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.marklog.blog.domain.post.tag.PostTag;

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

	@Column(length = 50, nullable = false, unique = true)
	private String name;

	@OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
	private List<PostTag> postTags = new ArrayList<>();
	
	@Builder
	public Tag(String name) {
		this.name = name;
	}
}
