package com.marklog.blog.service;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.marklog.blog.config.auth.dto.OAuthAttributes;
import com.marklog.blog.config.auth.dto.UserAuthenticationDto;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;
import com.marklog.blog.dto.UserResponseDto;
import com.marklog.blog.dto.UserUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;

	public Page<UserResponseDto> findAll(Pageable pageable) {
		Page<UserResponseDto> pageUserResponseDto = userRepository.findAll(pageable).map(UserResponseDto::new);
		return pageUserResponseDto;
	}

	public UserResponseDto findById(Long id) {
		User entity = userRepository.findById(id).orElseThrow();
		return new UserResponseDto(entity);
	}

	@Transactional
	public void update(Long id, UserUpdateRequestDto userUpdateRequestDto) {
		User user = userRepository.findById(id).orElseThrow();
		user.update(userUpdateRequestDto.getName(), userUpdateRequestDto.getPicture(), userUpdateRequestDto.getTitle(),
				userUpdateRequestDto.getIntroduce());
	}

	@Transactional
	public void delete(Long id) {
		userRepository.deleteById(id);
	}

	@Transactional
	public User saveOrUpdate(OAuthAttributes attributes) {
		User user = userRepository.findByEmail(attributes.getEmail()).map(
				entity -> entity.update(attributes.getName(), attributes.getPicture(), attributes.getTitle(), null))
				.orElse(attributes.toEntity());

		return userRepository.save(user);
	}

	public UserAuthenticationDto findAuthenticationDtoById(Long id) {
		User entity = userRepository.findById(id).orElseThrow();
		return new UserAuthenticationDto(entity);
	}
}
