package com.marklog.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.postlike.PostLike;
import com.marklog.blog.domain.postlike.PostLikeRepository;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class PostLikeServiceTest {
	@Mock
	UserRepository userRepository;

	@Mock
	PostRepository postRepository;

	@Mock
	PostLikeRepository postLikeRepository;

	PostLikeService postLikeService;

	User user;
	Post post;
	Long userId = 1L;
	Long postId = 2L;

	@BeforeEach
	public void setUp() {
		String name = "name";
		String email = "test@gmail.com";
		String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String userTitle = "myblog";
		String introduce = "introduce";
		user = new User(name, email, picture, userTitle, introduce, Role.USER);

		String title = "title";
		String content = "![](https://velog.velcdn.com/images/padomay1352/post/aa716ab1-e079-406b-ae82-c4489e7b95d1/image.png)\r\n"
				+ "# adsadasd as sa dsa dad ada s dsa\r\n"
				+ "hihihi thithithiad sad sa dasd sa dsad da a dsasasdsaa a sa sa saa sa  ad  ada\r\n"
				+ "asdad asd sa dsa dsa sad a dad  a  s as dsa dd sa da sa dsa sa dsa asd sa dsa\r\n";
		post = spy(new Post(null, null, title, content, user, null));
		postLikeService = new PostLikeService(postRepository, userRepository, postLikeRepository);
	}

	@Test
	public void testPostLikeSave() {
		// given
		when(postRepository.getReferenceById(postId)).thenReturn(post);
		when(userRepository.getReferenceById(userId)).thenReturn(user);

		// when
		postLikeService.save(postId, userId);

		// then
		verify(postLikeRepository).save(any());
	}

	@Test
	public void testPostLikeFindById() {
		// given
		PostLike postLike = new PostLike(post, user);
		Optional<PostLike> optionalPostLike = Optional.of(postLike);
		when(postLikeRepository.findById(any())).thenReturn(optionalPostLike);

		// when
		Boolean like = postLikeService.findById(postId, userId);

		// then
		assertThat(like).isTrue();
	}

	@Test
	public void testPostLikeDelete() {
		// given
		PostLike postLike = new PostLike(post, user);
		Optional<PostLike> optionalPostLike = Optional.of(postLike);
		when(postLikeRepository.findById(any())).thenReturn(optionalPostLike);

		// when
		postLikeService.delete(postId, userId);

		// then

	}

}
