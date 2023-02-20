package com.marklog.blog.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class UserRepositoryTest {
	@Autowired
	UserRepository userRepository;

	public static String name = "name";
	public static String email = "test@gmail.com";
	public static String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
	public static String title = "title";
	public static String introduce = "introduce";


    @Test
	public void testFindAllRepository() {
		//given
		User user = new User(name, email, picture, title, introduce, Role.USER);
		userRepository.save(user);
		user = new User(name, 1+email, picture, title, introduce, Role.USER);
		userRepository.save(user);
		user = new User(name, 2+email, picture, title, introduce, Role.USER);
		userRepository.save(user);
		user = new User(name, 3+email, picture, title, introduce, Role.USER);
		userRepository.save(user);

		//when
		PageRequest pageRequest = PageRequest.of(0, 4);
		Page<User> page = userRepository.findAll(pageRequest);
		List<User> userList = page.getContent();
		//then
		assertThat(userList.get(0).getEmail()).isEqualTo(email);
		assertThat(userList.get(1).getEmail()).isEqualTo(1+email);
		assertThat(page.getTotalElements()).isEqualTo(4);
		assertThat(page.getTotalPages()).isEqualTo(1);

	}

	@Test
	public void testSaveUserRepository() {
		//given
		User user = new User(name, email, picture, title, introduce, Role.USER);

		// when
		User savedUser = userRepository.save(user);
        // then

		assertThat(savedUser).isSameAs(user);
	}

    @Test
	public void testFindByIduserRepository() {
		//given
		User user = new User(name, email, picture, title, introduce, Role.USER);

		//when
		User savedUser = userRepository.save(user);
		User foundUser = userRepository.findById(savedUser.getId()).get();

		//then
		assertThat(savedUser).isSameAs(foundUser);
		assertThrows(IllegalArgumentException.class, () -> userRepository.findById(-1L).orElseThrow(() -> new IllegalArgumentException()));
		assertThrows(IllegalArgumentException.class, () -> userRepository.findById(-5L).orElseThrow(() -> new IllegalArgumentException()));
		assertThrows(IllegalArgumentException.class, () -> userRepository.findById(1000L).orElseThrow(() -> new IllegalArgumentException()));
		assertThrows(IllegalArgumentException.class, () -> userRepository.findById(20000L).orElseThrow(() -> new IllegalArgumentException()));
	}

    @Test
	public void testDeleteUserReposeitory() {
		//given
		User user = new User(name, email, picture, title, introduce, Role.USER);

		//when
		User savedUser = userRepository.save(user);
		userRepository.delete(user);

		//then
		assertThrows(IllegalArgumentException.class, () -> userRepository.findById(savedUser.getId()).orElseThrow(() -> new IllegalArgumentException()));

	}

}
