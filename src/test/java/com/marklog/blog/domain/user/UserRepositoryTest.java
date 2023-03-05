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


	public User createUser() {
		User user = new User(name, email, picture, title, introduce, Role.USER);
		return userRepository.save(user);
	}
	
    @Test
	public void testFindAllRepository() {
		//given
    	User user = createUser();
    	String newEmail = 1+email;
		user = new User(name, newEmail, picture, title, introduce, Role.USER);
		userRepository.save(user);

		//when
		PageRequest pageRequest = PageRequest.of(0, 4);
		Page<User> page = userRepository.findAll(pageRequest);
		List<User> userList = page.getContent();

		//then
		assertThat(userList.get(0).getEmail()).isEqualTo(email);
		assertThat(userList.get(1).getEmail()).isEqualTo(newEmail);
		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getTotalPages()).isEqualTo(1);

	}

	@Test
	public void testSaveUserRepository() {
		//given
		// when
		User user = createUser();

		// then
		assertThat(user.getId()).isGreaterThan(0);
	}

    @Test
	public void testFindByIdUserRepository() {
		//given
    	User user = createUser();
		
		//when
		User foundUser = userRepository.findById(user.getId()).get();

		//then
		assertThat(user).isSameAs(foundUser);
	}
    
    @Test
    public void testUpdateUserRepository() {
    	//then
    	User user = createUser();
    	String newName = 1+name;
    	String newPicture = 1+picture;
    	String newTitle = 1+title;
    	String newIntroduce = 1+introduce;

		//given
    	user.update(newName, newPicture, newTitle, newIntroduce);
    	
    	//then-ready
		User foundUser = userRepository.findById(user.getId()).get();
		assertThat(foundUser.getName()).isEqualTo(newName);
		assertThat(foundUser.getPicture()).isEqualTo(newPicture);
		assertThat(foundUser.getTitle()).isEqualTo(newTitle);
		assertThat(foundUser.getIntroduce()).isEqualTo(newIntroduce);
    }

    @Test
	public void testDeleteUserRepository() {
		//given
		User user = new User(name, email, picture, title, introduce, Role.USER);
		User savedUser = userRepository.save(user);
		
		//when
		userRepository.delete(user);

		//then
		assertThrows(IllegalArgumentException.class, () -> userRepository.findById(savedUser.getId()).orElseThrow(() -> new IllegalArgumentException()));

	}

}
