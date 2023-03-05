package com.marklog.blog.domain.postlike;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;

@Transactional
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class PostLikeRepositoryTest {
	@Autowired
	PostRepository postRepository;
	@Autowired
	UserRepository userRepository;
	Post post;
	User user;

	@Autowired
	PostLikeRepository postLikeRepository;

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

	@Transactional
	public PostLike createPostLike() {
		PostLike postLike = new PostLike(post, user);
		postLikeRepository.save(postLike);
		return postLike;
	}

	@Test
	public void testSavePostLike() {
		// given
		// when
		PostLike postLike = createPostLike();
		postLike.getPost().getPostLikes().add(postLike);

		// then
		assertThat(postLike.getPost().getPostLikes().get(0)).isEqualTo(postLike);
	}

	@Test
	public void testSavePostRepository_post의_like_count_확인() {
		// given
		// when
		PostLike postLike = createPostLike();
		postLike.getPost().getPostLikes().add(postLike);

		// then-ready
		Post findPost = postRepository.findById(post.getId()).get();
		// then
		assertThat(findPost.getPostLikes().size()).isEqualTo(1);
	}

	@Test
	public void testFindByIdPostLike() {
		// given
		PostLike postLike = createPostLike();

		// when
		PostLike returnPostLike = postLikeRepository.findById(new PostLikeIdClass(post.getId(), user.getId())).get();

		// then
		assertThat(returnPostLike).usingRecursiveComparison().isEqualTo(postLike);
	}

	@Test
	public void testFindPostCountByPostId() {
		// given
		createPostLike();

		// when
		Long postLikeCout = postLikeRepository.getPostLikeCountByPostId(post.getId());

		// then
		assertThat(postLikeCout).isEqualTo(1L);
	}

	@Test
	public void testPostLikeDelete() {
		// given
		PostLike postLike = createPostLike();

		// when
		postLikeRepository.delete(postLike);

		// then-ready
		PostLikeIdClass id = new PostLikeIdClass(post.getId(), user.getId());

		// then
		assertThrows(IllegalArgumentException.class,
				() -> postLikeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException()));
	}

}
