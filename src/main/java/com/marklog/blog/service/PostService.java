package com.marklog.blog.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.tag.Tag;
import com.marklog.blog.domain.tag.TagRepository;
import com.marklog.blog.domain.user.Users;
import com.marklog.blog.domain.user.UsersRepository;
import com.marklog.blog.web.dto.PostResponseDto;
import com.marklog.blog.web.dto.PostSaveRequestDto;
import com.marklog.blog.web.dto.PostUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostService {
	private final PostRepository postRepository;
	private final UsersRepository usersRepository;
	private final TagRepository tagRepository;

	@Transactional
	public Long save(PostSaveRequestDto requestDto) {
		Users user = usersRepository.getReferenceById(requestDto.getUserId());
		List<String> tagNames = requestDto.getTags();


		Post post = Post.builder().title(requestDto.getTitle()).content(requestDto.getContent()).user(user).build();
		post = postRepository.save(post);
		if(tagNames != null) {
			for(String tagName: tagNames) {
				Tag tag = Tag.builder().name(tagName).post(post).build();
				tagRepository.save(tag);
			}
		}
		return post.getId();
	}

	public PostResponseDto findById(Long id) {
		Post entity = postRepository.findById(id).orElseThrow(()->new IllegalArgumentException("존재하지않는 id입니다="+id));
		return new PostResponseDto(entity);
	}

	@Transactional
	public void update(Long id, PostUpdateRequestDto requestDto) {
		Post post = postRepository.findById(id).orElseThrow(()->new IllegalArgumentException("존재하지않는 id입니다="+id));
		post.update(requestDto.getTitle(), requestDto.getContent());

		List<Tag> tags = tagRepository.findByPost(post);
		for(Tag tag : tags) {
			tagRepository.delete(tag);
		}
		List<String> tagNames = requestDto.getTagNames();
		if(tagNames != null) {
			for(String tagName: tagNames) {
				tagRepository.save(Tag.builder().name(tagName).post(post).build());
			}
		}
	}

	public void delete(Long id) {
		postRepository.deleteById(id);
	}

}
