package com.marklog.blog.service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.post.QPost;
import com.marklog.blog.domain.post.tag.QPostTag;
import com.marklog.blog.domain.tag.QTag;
import com.marklog.blog.domain.tag.Tag;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;
import com.marklog.blog.dto.PostListResponseDto;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class SearchService {
	@PersistenceContext
	private EntityManager entityManager;

	private final PostRepository postRepository;
	private final UserRepository userRepository;

	public Page<PostListResponseDto> search(Pageable pageable, String[] keywords) {
		QPost qpost = QPost.post;
		BooleanExpression predicate = null;
		for (String keyword : keywords) {
			if (predicate == null) {
				predicate = qpost.content.containsIgnoreCase(keyword).or(qpost.title.containsIgnoreCase(keyword));
			} else {
				predicate = predicate.or(qpost.content.containsIgnoreCase(keyword))
						.or(qpost.title.containsIgnoreCase(keyword));
			}
		}

		// when
		Page<PostListResponseDto> page = postRepository.findAll(predicate, pageable).map(PostListResponseDto::new);
		return page;
	}
	public Page<PostListResponseDto> searchByUserId(Pageable pageable, Long userId) {
		User user = userRepository.getReferenceById(userId);
		return postRepository.findAllByUserOrderByIdDesc(pageable, user).map(PostListResponseDto::new);
	}
	public Page<PostListResponseDto> searchByTag(Pageable pageable, String tag) {
		JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

		QTag qtag = QTag.tag;
		QPostTag qPostTag = QPostTag.postTag;
		QPost qpost = QPost.post;

		BooleanExpression predicate = null;
		predicate = qtag.name.containsIgnoreCase(tag);

		List<Post> posts = jpaQueryFactory.selectFrom(qpost).join(qpost.postTags, qPostTag).join(qPostTag.tag, qtag)
				.where(predicate).offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

		Long count = jpaQueryFactory.select(qpost.count()).from(qpost).join(qpost.postTags, qPostTag)
				.join(qPostTag.tag, qtag).where(predicate).fetchOne();

		List<PostListResponseDto> postLists = posts.stream().map(PostListResponseDto::new).collect(Collectors.toList());
		return new PageImpl<>(postLists, pageable, count);
	}

	public Page<PostListResponseDto> search(Pageable pageable, Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

}
