package com.marklog.blog.domain.post.tag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marklog.blog.domain.post.Post;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
	public List<PostTag> findAllByPost(Post post);
}
