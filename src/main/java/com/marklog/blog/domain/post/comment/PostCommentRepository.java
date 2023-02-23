package com.marklog.blog.domain.post.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marklog.blog.domain.post.Post;

public interface PostCommentRepository extends JpaRepository<PostComment, Long>{
	List<PostComment> findAllByPostAndParentIsNull(Post post);
}
