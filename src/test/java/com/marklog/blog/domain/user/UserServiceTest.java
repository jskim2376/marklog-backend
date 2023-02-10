package com.marklog.blog.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
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
	UsersRepository userRepository;
	
	public static Long id = 1L;
	public static LocalDateTime time;
	public static String name = "name";
	public static String email = "test@gmail.com";
	public static String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
	public static String title = "title";
	public static String introduce = "introduce";

	@BeforeAll
	public static void setUp() {
		time = LocalDateTime.now();
	}

	@Test
	public void testFindByIdUserService() {
		//given
		Optional<Users> user = Optional.of(new Users(name, email, picture, title, introduce, Role.USER));
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
		Optional<Users> user = Optional.of(new Users(name, email, picture, title, introduce, Role.USER));
		when(userRepository.findById(id)).thenReturn(user);
		UserService userService = new UserService(userRepository);

		
		String newName = "newName";
		String newPicture = "newPicture";
		String newTitle = "newTitle";
		String newIntroduce = "newIntroduce";
		
		
		//when
		UserResponseDto userServiceResponseDto = userService.update(id, new UserUpdateRequestDto(newName, newPicture, newTitle, newIntroduce));
		UserResponseDto userServiceTestDto = new UserResponseDto(null, null, email, newName, newPicture, newTitle, newIntroduce);
		
		//then
		assertThat(userServiceResponseDto).isEqualTo(userServiceTestDto);
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
