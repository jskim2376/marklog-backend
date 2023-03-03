package com.marklog.blog.domain.tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;

@DataJpaTest
public class TagRepositoryTest {
	@Autowired
	PostRepository postRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TagRepository tagRepository;

	String title = "title";
	String content = "title";

	String name = "name";
	String email = "test@gmail.com";
	String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
	String userTitle = "myblog";
	String introduce = "introduce";

	@Test
	public void testTagSave() {
		// given
		User user = new User(name, email, picture, title, introduce, Role.USER);
		userRepository.save(user);

		Post post = new Post(null,null,title, content, user, null);
		postRepository.save(post);

		Tag tag = new Tag(post, "new tage");

		// when
		Tag returnTag = tagRepository.save(tag);

		// then
		assertThat(returnTag).usingRecursiveComparison().isEqualTo(tag);
	}

	@Test
	public void testTagDelete() {
		// given
		User user = new User(name, email, picture, title, introduce, Role.USER);
		userRepository.save(user);

		Post post = new Post(null,null,title, content, user, null);
		postRepository.save(post);

		Tag tag = new Tag(post, "new tage");
		Tag returnTag = tagRepository.save(tag);

		// when
		tagRepository.delete(returnTag);

		// then
		assertThrows(IllegalArgumentException.class,
				() -> tagRepository.findById(returnTag.getId()).orElseThrow(() -> new IllegalArgumentException()));
	}

}
