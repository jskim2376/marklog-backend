package com.marklog.blog.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.user.Users;
import com.marklog.blog.domain.user.UsersRepository;
import com.marklog.blog.web.dto.PostResponseDto;
import com.marklog.blog.web.dto.PostSaveRequestDto;
import com.marklog.blog.web.dto.PostUpdateRequestDto;
import com.marklog.blog.web.dto.UserResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UsersRepository usersRepository;
	
	@Transactional
	public Long update(Long id, PostUpdateRequestDto requestDto) {
		Users user = usersRepository.findById(id).orElseThrow(()->new IllegalArgumentException("존재하지않는 id입니다="+id));
		user.update(requestDto.getTitle(), requestDto.getContent());
		return id;
	}
	
	public UserResponseDto findById(Long id) {
		Users entity = usersRepository.findById(id).orElseThrow(()->new IllegalArgumentException("존재하지않는 id입니다="+id));
		return new UserResponseDto(entity);
	}
}
