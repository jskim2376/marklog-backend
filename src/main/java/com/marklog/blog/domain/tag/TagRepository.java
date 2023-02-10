package com.marklog.blog.domain.tag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marklog.blog.domain.post.Post;

public interface TagRepository extends JpaRepository<Tag, Long>{

		Tag findByNameAndPost(String name, Post post);
		List<Tag> findByPost(Post post);


}
