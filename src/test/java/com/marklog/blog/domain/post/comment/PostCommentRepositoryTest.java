package com.marklog.blog.domain.post.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

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
public class PostCommentRepositoryTest {
	@Autowired
	UserRepository userRepository;
	@Autowired
	PostRepository postRepository;
	Post post;
	User user;

	@Autowired
	PostCommentRepository postCommentRepository;
	String commentContent = "comment content";

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
		post = new Post(thumbnail, summary, title, content, user);
		postRepository.save(post);
	}

	public PostComment createPostComment() {
		PostComment postComment = new PostComment(post, user, commentContent);
		postCommentRepository.save(postComment);
		return postComment;
	}

	@Test
	public void testPostCommentFindAllByPost() {
		// given
		PostComment savedPostComment1 = createPostComment();
		PostComment savedPostComment2 = createPostComment();

		// when
		List<PostComment> findCommentList = postCommentRepository.findAllByPostAndParentIsNull(post);

		// then-ready
		PostComment findPostComment1 = findCommentList.get(0);
		PostComment findPostComment2 = findCommentList.get(1);

		// then
		assertThat(findPostComment1).isEqualTo(savedPostComment1);
		assertThat(findPostComment2).isEqualTo(savedPostComment2);
	}

	@Test
	public void testPostCommentSave() {
		// given
		PostComment postComment = new PostComment(post, user, commentContent);

		// when
		PostComment savedPostComment = postCommentRepository.save(postComment);

		// then
		assertThat(savedPostComment.getId()).isNotNull();
	}

	@Test
	public void testPostCommentSave_child() {
		// given
		PostComment postComment = createPostComment();
		String childContent = "childContent";
		PostComment childPostComment = new PostComment(post, user, childContent);

		// when
		postCommentRepository.save(childPostComment);
		childPostComment.setParent(postComment);

		// then
		assertThat(postComment.getChildList().get(0)).isSameAs(childPostComment);
	}

	@Test
	public void testPostCommentFindById() {
		// given
		PostComment postComment = createPostComment();

		// when
		PostComment findComment = postCommentRepository.findById(postComment.getId()).get();

		// then
		assertThat(postComment).isSameAs(findComment);
	}

	@Test
	public void testPostCommentFindById_with_child() {
		// given
		PostComment postComment = createPostComment();
		String childContent = "childContent";
		PostComment childPostComment = new PostComment(post, user, childContent);
		postCommentRepository.save(childPostComment);
		childPostComment.setParent(postComment);

		// when
		PostComment findComment = postCommentRepository.findById(postComment.getId()).get();

		// then
		assertThat(postComment).isSameAs(findComment);
		assertThat(findComment.getChildList().get(0)).isSameAs(childPostComment);
	}

	@Test
	public void testPostCommentUpdate() {
		// given
		PostComment postComment = createPostComment();
		String newContent = "new content";

		// when
		postComment.update(newContent);

		// then-ready
		PostComment updateComment = postCommentRepository.findById(postComment.getId()).get();

		// then
		assertThat(updateComment).isSameAs(postComment);
		assertThat(updateComment.getContent()).isEqualTo(newContent);
	}

	@Test
	public void testPostCommentDelete() {
		// given
		PostComment savedPostComment = createPostComment();

		// when
		postCommentRepository.delete(savedPostComment);

		// then
		assertThrows(IllegalArgumentException.class, () -> postCommentRepository.findById(savedPostComment.getId())
				.orElseThrow(() -> new IllegalArgumentException()));
	}

	@Test
	public void testPostCommentDelete_with_child() {
		// given
		PostComment postComment = createPostComment();
		String childContent = "childContent";
		PostComment childPostComment = new PostComment(post, user, childContent);
		childPostComment.addChildComment(childPostComment);
		postCommentRepository.save(childPostComment);

		// when
		postComment.addChildComment(childPostComment);
		postCommentRepository.delete(postComment);

		// then
		assertThrows(IllegalArgumentException.class, () -> postCommentRepository.findById(postComment.getId())
				.orElseThrow(() -> new IllegalArgumentException()));
		assertThrows(IllegalArgumentException.class, () -> postCommentRepository.findById(childPostComment.getId())
				.orElseThrow(() -> new IllegalArgumentException()));
	}
}
