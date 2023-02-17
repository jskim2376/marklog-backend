package com.marklog.blog.domain.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.marklog.blog.domain.tag.Tag;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;

@DataJpaTest
public class PostRepostioryTest {
	@Autowired
	PostRepository postRepository;

	@Autowired
	UserRepository userRepository;


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
		User user = new User(name,email, picture, userTitle, introduce, Role.USER);
		userRepository.save(user);

		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag(null, "tag1"));
		tags.add(new Tag(null, "tag2"));

		Post post = new Post(title, content, user, tags);

		//when
		Post savedPost = postRepository.save(post);

		//then
		assertThat(savedPost.getCreatedDate()).isAfter(now);
		assertThat(savedPost.getModifiedDate()).isAfter(now);
	}

	@Test
	public void testSavePostRepository() {
		//given
		User user = new User(name,email, picture, userTitle, introduce, Role.USER);
		userRepository.save(user);

		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag(null, "tag1"));
		tags.add(new Tag(null, "tag2"));

		Post post = new Post(title, content, user, tags);

		//when
		Post savedPost = postRepository.save(post);

		//then
		assertThat(savedPost).isSameAs(post);
	}

	@Test
	public void testSavePostRepository_Users_optional_테스트() {
		//given
		User user = new User(name,email, picture, userTitle, introduce, Role.USER);

		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag(null, "tag1"));
		tags.add(new Tag(null, "tag2"));

		Post post = new Post(title, content, user, tags);

		//when
		//then
		assertThrows(InvalidDataAccessApiUsageException.class, () -> postRepository.save(post));
	}

    @Test
	public void testFindAllPostRepository() {
		//given
		User user = new User(name, email, picture, title, introduce, Role.USER);
		userRepository.save(user);

		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag(null, "tag1"));
		tags.add(new Tag(null, "tag2"));

		Post post = new Post(title, content, user, tags);
		postRepository.save(post);

		//when
		PageRequest pageRequest = PageRequest.of(0, 4);
		Page<Post> page = postRepository.findAll(pageRequest);
		List<Post> postList = page.getContent();

		//then
		assertThat(postList.get(0).getTitle()).isEqualTo(title);
		assertThat(postList.get(0).getContent()).isEqualTo(content);
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getTotalPages()).isEqualTo(1);

	}

	@Test
	public void testFindByIdPostRepostiroy() {
		//given
		User user = new User(name,email, picture, userTitle, introduce, Role.USER);
		userRepository.save(user);

		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag(null, "tag1"));
		tags.add(new Tag(null, "tag2"));

		Post post = new Post(title, content, user, tags);
		Post savedPost = postRepository.save(post);

		//when
		Post findPost = postRepository.findById(savedPost.getId()).get();

		//then
		assertThat(findPost.getTags().get(0).getName()).isEqualTo("tag1");
		assertThat(savedPost).isSameAs(findPost);
	}


    @Test
	public void testDeletePostReposeitory() {
		//given
		User user = new User(name, email, picture, title, introduce, Role.USER);
		userRepository.save(user);

		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag(null, "tag1"));
		tags.add(new Tag(null, "tag2"));

		Post post = new Post(title, content, user, tags);
		Post savedPost = postRepository.save(post);

		//when
		postRepository.delete(savedPost);

		//then
		assertThrows(IllegalArgumentException.class, () -> postRepository.findById(savedPost.getId()).orElseThrow(() -> new IllegalArgumentException()));

	}

}
