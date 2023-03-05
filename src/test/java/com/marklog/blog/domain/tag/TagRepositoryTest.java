package com.marklog.blog.domain.tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
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
	UserRepository userRepository;
	@Autowired
	PostRepository postRepository;
	User user;
	Post post;

	@Autowired
	TagRepository tagRepository;

	@BeforeEach
	public void setupEach() {
		String name = "name";
		String email = "test@gmail.com";
		String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String userTitle = "myblog";
		String introduce = "introduce";
		user = new User(name, email, picture, userTitle, introduce, Role.USER);
		userRepository.save(user);

		String thumbnail = "thumbnail";
		String summary = "summary";
		String title = "title";
		String content = "title";
		post = new Post(thumbnail, summary, title, content, user, null);
		postRepository.save(post);
	}
	
	public Tag createTag() {
		Tag tag = new Tag(post, "new tage");
		return tagRepository.save(tag);
	}

	@Test
	public void testTagSave() {
		// given
		// when
		Tag tag = createTag();

		// then
		assertThat(tag.getId()).isGreaterThan(0);
	}

	@Test
	public void testTagDelete() {
		// given
		Tag tag = createTag();

		// when
		tagRepository.delete(tag);

		// then
		assertThrows(IllegalArgumentException.class,
				() -> tagRepository.findById(tag.getId()).orElseThrow(() -> new IllegalArgumentException()));
	}

}
