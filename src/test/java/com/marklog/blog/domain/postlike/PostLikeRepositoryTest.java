package com.marklog.blog.domain.postlike;

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
	public void testSavePostLike() {
		//given

		User user = new User(name, email, picture, title, introduce, Role.USER);
		userRepository.save(user);

		Post post = new Post(null,null,title, content, user, null);
		postRepository.save(post);


		PostLike postLike = new PostLike(post, user);

		//when
		PostLike returnPostLike = postLikeRepository.save(postLike);

		//then
		assertThat(returnPostLike).usingRecursiveComparison().isEqualTo(postLike);
	}

	@Test
	public void testFindByIdPostLike() {
		//given

		User user = new User(name, email, picture, title, introduce, Role.USER);
		userRepository.save(user);

		Post post = new Post(null,null,title, content, user, null);
		postRepository.save(post);


		PostLike postLike = new PostLike(post, user);
		postLikeRepository.save(postLike);

		//when
		PostLike returnPostLike = postLikeRepository.findById(new PostLikeIdClass(post.getId(), user.getId())).get();

		//then
		assertThat(returnPostLike).usingRecursiveComparison().isEqualTo(postLike);
	}


	@Test
	public void testPostLikeDelete() {
		//given

		User user = new User(name, email, picture, title, introduce, Role.USER);
		userRepository.save(user);

		Post post = new Post(null,null,title, content, user, null);
		postRepository.save(post);

		PostLike postLike = new PostLike(post, user);
		postLikeRepository.save(postLike);

		//when
		postLikeRepository.delete(postLike);

		PostLikeIdClass id = new PostLikeIdClass(post.getId(), user.getId());

		//then
		assertThrows(IllegalArgumentException.class, () -> postLikeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException()));
	}

}
