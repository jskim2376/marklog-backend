package com.marklog.blog.postlike;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.postlike.PostLike;
import com.marklog.blog.domain.postlike.PostLikeIdClass;
import com.marklog.blog.domain.postlike.PostLikeRepository;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;

@DataJpaTest
public class PostLikeRepositoryTest {
	@Autowired
	PostRepository postRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PostLikeRepository postLikeRepository;

	String title = "title";
	String content = "title";

	String name = "name";
	String email = "test@gmail.com";
	String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
	String userTitle = "myblog";
	String introduce = "introduce";

	@Test
	public void testSavepostLike() {
		//given

		User user = new User(name, email, picture, title, introduce, Role.USER);
		userRepository.save(user);

		Post post = new Post(title, content, user, null);
		postRepository.save(post);


		PostLike postLike = new PostLike(post, user);

		//when
		PostLike returnPostLike = postLikeRepository.save(postLike);

		//then
		assertThat(returnPostLike).isSameAs(postLike);
	}



	@Test
	public void testPostLikeDelete() {
		//given

		User user = new User(name, email, picture, title, introduce, Role.USER);
		userRepository.save(user);

		Post post = new Post(title, content, user, null);
		postRepository.save(post);

		PostLike postLike = new PostLike(post, user);
		PostLike returnPostLike = postLikeRepository.save(postLike);

		PostLikeIdClass postLikeIdClass = new PostLikeIdClass(user.getId(), post.getId());
		//when
		postLikeRepository.deleteById(postLikeIdClass);

		//then
		assertThrows(IllegalArgumentException.class, () -> postLikeRepository.findById(postLikeIdClass).orElseThrow(() -> new IllegalArgumentException()));

		assertThat(returnPostLike).isSameAs(postLike);
	}

}
