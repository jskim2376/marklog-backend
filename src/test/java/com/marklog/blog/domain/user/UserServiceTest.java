package com.marklog.blog.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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

import com.marklog.blog.service.UserService;
import com.marklog.blog.web.dto.UserResponseDto;
import com.marklog.blog.web.dto.UserUpdateRequestDto;

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
		//given
		List<User> content = new ArrayList<>();
		User user = new User(name, email, picture, title, introduce, Role.USER);
		content.add(user);
		
		int pageCount = 0;
		int size=20;
		Pageable pageable = PageRequest.of(pageCount, size);
		
		Page<User> page = new PageImpl<>(content, pageable, 1);
		when(userRepository.findAll(pageable)).thenReturn(page);
		
		UserService userService = new UserService(userRepository);

		//when
		Page<UserResponseDto> pageUserResponseDto =  userService.findAll(pageable);

		//then
		assertThat(pageUserResponseDto.getContent().get(0).getName()).isEqualTo(name);
		assertThat(pageUserResponseDto.getContent().get(0).getEmail()).isEqualTo(email);
		assertThat(pageUserResponseDto.getContent().get(0).getPicture()).isEqualTo(picture);
		assertThat(pageUserResponseDto.getContent().get(0).getTitle()).isEqualTo(title);
		assertThat(pageUserResponseDto.getContent().get(0).getIntroduce()).isEqualTo(introduce);
		assertThat(pageUserResponseDto.getSize()).isEqualTo(size);
		assertThat(pageUserResponseDto.getTotalElements()).isEqualTo(1);
		
	}
	@Test
	public void testFindByIdUserService() {
		//given
		Optional<User> user = Optional.of(new User(name, email, picture, title, introduce, Role.USER));
		when(userRepository.findById(id)).thenReturn(user);
		UserService userService = new UserService(userRepository);

		//when
		UserResponseDto userServiceResponseDto = userService.findById(id);
		UserResponseDto userServiceTestDto = new UserResponseDto(user.get());

		//then
		assertThat(userServiceResponseDto).isEqualTo(userServiceTestDto);
	}


	@Test
	public void testUpdateUserService() {
		//given
		UserService userService = new UserService(userRepository);

		Optional<User> user = Optional.of(new User(name, email, picture, title, introduce, Role.USER));
		when(userRepository.findById(id)).thenReturn(user);

		String newName = "newName";
		String newPicture = "newPicture";
		String newTitle = "newTitle";
		String newIntroduce = "newIntroduce";

		//when
		userService.update(id, new UserUpdateRequestDto(newName, newPicture, newTitle, newIntroduce));
		//then
		verify(userRepository).findById(anyLong());
	}

	@Test
	public void testDeleteUserService() {
		//given
		UserService userService = new UserService(userRepository);

		//when
		userService.delete(id);

		//then
		Mockito.verify(userRepository).deleteById(id);

	}

}
