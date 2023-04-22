package com.marklog.blog.domain.post.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeIdClass> {
	@Query("select count(p) from PostLike p where p.post.id=:postId")
	Long getPostLikeCountByPostId(@Param("postId") Long postId);
}
