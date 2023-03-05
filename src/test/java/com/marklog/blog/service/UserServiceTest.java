package com.marklog.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.marklog.blog.config.auth.dto.OAuthAttributes;
import com.marklog.blog.config.auth.dto.UserAuthenticationDto;
import com.marklog.blog.controller.dto.UserResponseDto;
import com.marklog.blog.controller.dto.UserUpdateRequestDto;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {
	@MockBean
	UserRepository userRepository;

	public static Long id = 1L;
	public static String name = "name";
	public static String email = "test@gmail.com";
	public static String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
	public static String title = "title";
	public static String introduce = "introduce";

	@Test
	public void testFinAllUserService() {
		// given
		User user = new User(name, email, picture, title, introduce, Role.USER);

		List<User> content = new ArrayList<>();
		content.add(user);
		int pageCount = 0;
		int size = 20;
		Pageable pageable = PageRequest.of(pageCount, size);
		Page<User> page = new PageImpl<>(content, pageable, 1);
		when(userRepository.findAll(pageable)).thenReturn(page);

		UserService userService = new UserService(userRepository);

		// when
		Page<UserResponseDto> pageUserResponseDto = userService.findAll(pageable);

		// then
		assertThat(pageUserResponseDto.getContent().get(0).getName()).isEqualTo(name);
		assertThat(pageUserResponseDto.getContent().get(0).getEmail()).isEqualTo(email);
		assertThat(pageUserResponseDto.getContent().get(0).getPicture()).isEqualTo(picture);
		assertThat(pageUserResponseDto.getContent().get(0).getTitle()).isEqualTo(title);
		assertThat(pageUserResponseDto.getContent().get(0).getIntroduce()).isEqualTo(introduce);
		assertThat(pageUserResponseDto.getSize()).isEqualTo(size);
		assertThat(pageUserResponseDto.getTotalElements()).isEqualTo(1);

	}

	@Test
	public void testSaveOrUpdate() {
		// given
		UserService userService = new UserService(userRepository);
		OAuthAttributes oAuthAttributes = new OAuthAttributes(null, "id", name, email, picture, title);

		User user = new User(name, email, picture, title, introduce, Role.USER);
		Optional<User> optinal = Optional.of(user);
		when(userRepository.findByEmail(anyString())).thenReturn(optinal);
		when(userRepository.save(any())).thenReturn(user);

		// when
		User returnUser = userService.saveOrUpdate(oAuthAttributes);

		// then
		assertThat(returnUser.getName()).isEqualTo(name);
		assertThat(returnUser.getEmail()).isEqualTo(email);
		assertThat(returnUser.getPicture()).isEqualTo(picture);
		assertThat(returnUser.getTitle()).isEqualTo(title);
	}

	@Test
	public void testFindByIdUserService() {
		// given
		Optional<User> user = Optional.of(new User(name, email, picture, title, introduce, Role.USER));
		when(userRepository.findById(id)).thenReturn(user);
		UserService userService = new UserService(userRepository);

		// when
		UserResponseDto userServiceResponseDto = userService.findById(id);
		UserResponseDto userServiceTestDto = new UserResponseDto(user.get());

		// then
		assertThat(userServiceResponseDto).usingRecursiveComparison().isEqualTo(userServiceTestDto);
	}

	@Test
	public void testFindAuthenticationDtoById() {
		// given
		UserService userService = new UserService(userRepository);
		Optional<User> optinal = Optional.of(new User(name, email, picture, title, introduce, Role.USER));

		when(userRepository.findById(anyLong())).thenReturn(optinal);

		// when
		UserAuthenticationDto user = userService.findAuthenticationDtoById(id);

		// then
		assertThat(user.getEmail()).isEqualTo(email);
		assertThat(user.getRole()).isEqualTo(Role.USER);
	}

	@Test
	public void testUpdateUserService() {
		// given
		UserService userService = new UserService(userRepository);

		Optional<User> user = Optional.of(new User(name, email, picture, title, introduce, Role.USER));
		when(userRepository.findById(id)).thenReturn(user);

		String newName = "newName";
		String newPicture = "newPicture";
		String newTitle = "newTitle";
		String newIntroduce = "newIntroduce";

		// when
		userService.update(id, new UserUpdateRequestDto(newName, newPicture, newTitle, newIntroduce));
		// then
		verify(userRepository).findById(anyLong());
	}

	@Test
	public void testDeleteUserService() {
		// given
		UserService userService = new UserService(userRepository);

		// when
		userService.delete(id);

		// then
		Mockito.verify(userRepository).deleteById(id);
	}

}
