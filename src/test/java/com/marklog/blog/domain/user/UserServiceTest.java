package com.marklog.blog.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
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
