package com.marklog.blog.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryTest {
	@Autowired
	UsersRepository userRepository;

	public static String name = "name";
	public static String email = "test@gmail.com";
	public static String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
	public static String title = "title";
	public static String introduce = "introduce";		
	
	@Test
	public void testSaveUserRepository() {
		//given
		Users user = new Users(name, email, picture, title, introduce, Role.USER);

		// when
		Users savedUser = userRepository.save(user);
        // then
        
		assertThat(savedUser).isSameAs(user);
	}
	
    @Test
	public void testFindByIduserRepository() {
		//given
		Users user = new Users(name, email, picture, title, introduce, Role.USER);
		
		//when
		Users savedUser = userRepository.save(user);
		Users foundUser = userRepository.findById(savedUser.getId()).get();

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
		Users user = new Users(name, email, picture, title, introduce, Role.USER);
		
		//when
		Users savedUser = userRepository.save(user);
		userRepository.delete(user);

		//then
		assertThrows(IllegalArgumentException.class, () -> userRepository.findById(savedUser.getId()).orElseThrow(() -> new IllegalArgumentException()));
		
	}

}
