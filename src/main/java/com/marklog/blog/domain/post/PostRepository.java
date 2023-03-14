package com.marklog.blog.domain.post;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, QuerydslPredicateExecutor<Post> {
	@Query("select p from Post p inner join p.tags t where t.name=:tagName")
	List<Post> findAllByTagName(Pageable pageable, @Param("tagName") String tagName);

	@Query("select p from Post p inner join p.tags t where t.name=:tagName and p.user.id=:userId")
	List<Post> findAllByTagNameAndUserId(Pageable pageable, @Param("tagName") String tagName, @Param("userId") Long userId);
}
