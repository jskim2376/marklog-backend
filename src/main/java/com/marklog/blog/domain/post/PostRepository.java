package com.marklog.blog.domain.post;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.marklog.blog.domain.user.User;

public interface PostRepository extends JpaRepository<Post, Long>, QuerydslPredicateExecutor<Post> {
    public Page<Post> findAllByOrderByIdDesc(Pageable pageable);
    public Page<Post> findAllByUserOrderByIdDesc(Pageable pageable, User user);
}
