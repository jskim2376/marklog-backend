package com.marklog.blog.domain.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.Users;
import com.marklog.blog.domain.user.UsersRepository;

@DataJpaTest
public class PostRepostioryTest {
	@Autowired
	PostRepository postRepository;

	@Autowired
	UsersRepository usersRepository;


	String title="title";
	String content="title";

	String name = "name";
	String email = "test@gmail.com";
	String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
	String userTitle = "myblog";
	String introduce = "introduce";

	@Test
	public void testBaseTimeEntity() {
		//given
		LocalDateTime now = LocalDateTime.of(2019, 6,4,0,0,0);
		Users user = new Users(name,email, picture, userTitle, introduce, Role.USER);
		usersRepository.save(user);
		Post post = new Post(title, content, user);

		//when
		Post savedPost = postRepository.save(post);

		//then
		assertThat(savedPost.getCreatedDate()).isAfter(now);
		assertThat(savedPost.getModifiedDate()).isAfter(now);
	}

	@Test
	public void testSavePostRepository() {
		//given
		Users user = new Users(name,email, picture, userTitle, introduce, Role.USER);
		usersRepository.save(user);
		Post post = new Post(title, content, user);

		//when
		Post savedPost = postRepository.save(post);

		//then
		assertThat(savedPost).isSameAs(post);
	}

	@Test
	public void testSavePostRepository_Users_optional_테스트() {
		//given
		Users user = new Users(name,email, picture, userTitle, introduce, Role.USER);
		Post post = new Post(title, content, user);

		//when
		//then
		assertThrows(InvalidDataAccessApiUsageException.class, () -> postRepository.save(post));
	}

	@Test
	public void testFindByIdPostRepostiroy() {
		//given
		Users user = new Users(name,email, picture, userTitle, introduce, Role.USER);
		usersRepository.save(user);
		Post post = new Post(title, content, user);
		Post savedPost = postRepository.save(post);

		//when
		Post findPost = postRepository.findById(savedPost.getId()).get();

		//then
		assertThat(savedPost).isSameAs(findPost);
	}

	@Test
	public void testFindAllPostRepostiroy() {
		//given
		Users user = new Users(name,email, picture, userTitle, introduce, Role.USER);
		usersRepository.save(user);
		Post post = new Post(title, content, user);
		Post post2 = new Post(title+"2", content+"2", user);
		postRepository.save(post);
		postRepository.save(post2);

		//when
		List<Post> foundPostList = postRepository.findAll();

		//then
		assertThat(foundPostList.get(0)).isSameAs(post);
		assertThat(foundPostList.get(1)).isSameAs(post2);
	}




    @Test
	public void testDeletePostReposeitory() {
		//given
		Users user = new Users(name, email, picture, title, introduce, Role.USER);
		usersRepository.save(user);
		Post post = new Post(title, content, user);
		Post savedPost = postRepository.save(post);

		//when
		postRepository.delete(savedPost);

		//then
		assertThrows(IllegalArgumentException.class, () -> postRepository.findById(savedPost.getId()).orElseThrow(() -> new IllegalArgumentException()));

	}

}
