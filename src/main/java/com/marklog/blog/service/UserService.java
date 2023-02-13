package com.marklog.blog.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.marklog.blog.config.auth.dto.OAuthAttributes;
import com.marklog.blog.domain.user.Users;
import com.marklog.blog.domain.user.UsersRepository;
import com.marklog.blog.web.dto.UserAuthenticationDto;
import com.marklog.blog.web.dto.UserResponseDto;
import com.marklog.blog.web.dto.UserUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UsersRepository usersRepository;

	public UserResponseDto findById(Long id) {
		Users entity = usersRepository.findById(id).orElseThrow(()->new IllegalArgumentException("존재하지않는 id입니다="+id));
		return new UserResponseDto(entity);
	}

	@Transactional
	public UserResponseDto update(Long id, UserUpdateRequestDto userUpdateRequestDto) {
		Users user = usersRepository.findById(id).orElseThrow(()->new IllegalArgumentException("존재하지않는 id입니다="+id));
		user = user.update(userUpdateRequestDto.getName(), userUpdateRequestDto.getPicture(),  userUpdateRequestDto.getTitle(), userUpdateRequestDto.getIntroduce());

		return new UserResponseDto(user);
	}

	@Transactional
	public void delete(Long id) {
		usersRepository.deleteById(id);
	}
	
	@Transactional
    public Users saveOrUpdate(OAuthAttributes attributes){
        Users user = usersRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture(), attributes.getTitle(), null))
                .orElse(attributes.toEntity());

        return usersRepository.save(user);
    }

	public UserAuthenticationDto findAuthenticationDtoById(Long id) {
		Users entity = usersRepository.findById(id).orElseThrow(()->new IllegalArgumentException("존재하지않는 id입니다="+id));
		return new UserAuthenticationDto(entity);		
	}
}
