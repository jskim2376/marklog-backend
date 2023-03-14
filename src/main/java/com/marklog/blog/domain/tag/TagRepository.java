package com.marklog.blog.domain.tag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.dto.TagCountResponseDto;

public interface TagRepository extends JpaRepository<Tag, Long> {
	Tag findByNameAndPost(String name, Post post);
	List<Tag> findByPost(Post post);
	@Query("select new com.marklog.blog.dto.TagCountResponseDto(t.name, count(t.name)) from Tag t where t.post.user.id = :userId group by t.name")
	List<TagCountResponseDto> countTagNameByUserId(@Param("userId") Long userId);
}
