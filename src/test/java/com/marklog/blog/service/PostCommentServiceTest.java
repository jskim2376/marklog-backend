package com.marklog.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marklog.blog.controller.dto.PostCommentRequestDto;
import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.post.comment.PostComment;
import com.marklog.blog.domain.post.comment.PostCommentRepository;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class PostCommentServiceTest {
	@Mock
	UserRepository userRepository;

	@Mock
	PostRepository postRepository;

	@Mock
	PostCommentRepository postCommentRepository;

	User user;
	Long userId = 1L;
	String name = "name";
	String email = "test@gmail.com";
	String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
	String userTitle = "myblog";
	String introduce = "introduce";

	Post post;
	Long postId = 2L;
	String title = "title";
	String content = "title";
	PostCommentService postCommentService;

	Long postCommentId = 1L;
	String commentContent = "string";

	@BeforeEach
	public void setUp() {
		user = new User(name, email, picture, title, introduce, Role.USER);
		post = new Post(title, content, user, null);
		postCommentService = new PostCommentService(postRepository, userRepository, postCommentRepository);
	}

	@Test
	public void testPostCommentServiceSave() {
		// given
		when(postRepository.getReferenceById(postId)).thenReturn(post);
		when(userRepository.getReferenceById(userId)).thenReturn(user);
		
		PostComment postComment = spy(new PostComment(post, user, commentContent));
		when(postComment.getId()).thenReturn(postCommentId);
		when(postCommentRepository.save(any())).thenReturn(postComment);
		PostCommentRequestDto requestDto = new PostCommentRequestDto(commentContent);
		// when
		Long savedId = postCommentService.save(postId, userId, requestDto);

		// then
		verify(postCommentRepository).save(any());
		assertThat(savedId).isEqualTo(postCommentId);
	}
	
	@Test
	public void testPostCommentServiceFindAll() {
		// given
		when(postRepository.getReferenceById(postId)).thenReturn(post);

		PostComment postComment = new PostComment(post, user, commentContent);
		List<PostComment> postComments = new ArrayList<>();
		postComments.add(postComment);
		when(postCommentRepository.findAllByPost(any())).thenReturn(postComments);
		// when
		List<PostComment> findPostComments = postCommentService.findAll(postId);

		// then
		assertThat(findPostComments.get(0)).isSameAs(postComment);
	}

	@Test
	public void testPostCommentServiceFindById() {
		// given
		PostComment postComment = new PostComment(post, user, commentContent);
		Optional<PostComment> optinalPostComment = Optional.of(postComment);
		when(postCommentRepository.findById(any())).thenReturn(optinalPostComment);

		// when
		PostComment findPostComment = postCommentService.findById(postCommentId);

		// then
		assertThat(findPostComment).isSameAs(postComment);
	}

	@Test
	public void testPostCommentServiceUpdate() {
		// given
		PostComment postComment = new PostComment(post, user, commentContent);
		Optional<PostComment> optinalPostComment = Optional.of(postComment);
		when(postCommentRepository.findById(any())).thenReturn(optinalPostComment);
		
		String newContent = "update comment";
		PostCommentRequestDto requestDto = new PostCommentRequestDto(newContent);
		// when
		postCommentService.update(postCommentId, requestDto);

		// then
		assertThat(postComment.getContent()).isEqualTo(newContent);
	}

	@Test
	public void testPostCommentServiceDelete() {
		// given
		PostComment postComment = new PostComment(post, user, commentContent);
		Optional<PostComment> optinalPostComment = Optional.of(postComment);
		when(postCommentRepository.findById(any())).thenReturn(optinalPostComment);

		// when
		postCommentService.delete(postCommentId);
		// then
		verify(postCommentRepository).delete(postComment);

	}

}
