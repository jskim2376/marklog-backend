package com.marklog.blog.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.post.tag.PostTag;
import com.marklog.blog.domain.post.tag.PostTagRepository;
import com.marklog.blog.domain.tag.Tag;
import com.marklog.blog.domain.tag.TagRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostTagService {
	private final PostRepository postRepository;
	private final TagRepository tagRepository;
	private final PostTagRepository postTagRepository;
	
	@Transactional
	public void save(Long postId, Long tagId) {
		Post post = postRepository.getReferenceById(postId);
		Tag tag = tagRepository.getReferenceById(tagId);
		postTagRepository.save(new PostTag(post, tag));
	}

	@Transactional
	public List<PostTag> findAllByPostId(Long postId) {
		Post post = postRepository.getReferenceById(postId);
		return postTagRepository.findAllByPost(post);
	}

	@Transactional
	public void delete(Long id) {
		PostTag postTag = postTagRepository.getReferenceById(id);
		postTagRepository.delete(postTag);
	}
}
