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
import com.marklog.blog.controller.dto.PostCommentResponseDto;
import com.marklog.blog.controller.dto.PostCommentUpdateRequestDto;
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
		post = new Post(null,null, title, content, user, null);
		postCommentService = new PostCommentService(postRepository, userRepository, postCommentRepository);
	}

	@Test
	public void testPostCommentServiceSave() {
		// given
		PostCommentRequestDto requestDto = new PostCommentRequestDto(null, commentContent);

		when(postRepository.getReferenceById(postId)).thenReturn(post);
		when(userRepository.getReferenceById(userId)).thenReturn(user);
		when(postCommentRepository.save(any())).thenAnswer(invocation -> {
		    PostComment postcomment = (PostComment)(invocation.getArguments()[0]);
		    postcomment = spy(postcomment);
		    when(postcomment.getId()).thenReturn(postCommentId);
		    return postcomment;
		});
		// when
		Long savedId = postCommentService.save(postId, userId, requestDto);

		// then
		assertThat(savedId).isEqualTo(postCommentId);
	}

	@Test
	public void testPostCommentServiceSaveWithChild() {
		// given
		PostCommentRequestDto requestDto = new PostCommentRequestDto(postCommentId, commentContent);
		when(postRepository.getReferenceById(postId)).thenReturn(post);
		when(userRepository.getReferenceById(userId)).thenReturn(user);
		PostComment postComment = spy(new PostComment(post, user, commentContent+2));
		when(postCommentRepository.getReferenceById(postCommentId)).thenReturn(postComment);
		when(postCommentRepository.save(any())).thenAnswer(invocation -> {
		    PostComment postcomment = (PostComment)(invocation.getArguments()[0]);
		    postcomment = spy(postcomment);
		    when(postcomment.getId()).thenReturn(postCommentId);
		    return postcomment;
		});		
		// when
		Long savedId = postCommentService.save(postId, userId, requestDto);

		// then
		assertThat(savedId).isEqualByComparingTo(postCommentId);
		assertThat(postComment.getChildList().get(0).getContent()).isEqualTo(commentContent);
		assertThat(postComment.getChildList().get(0).getParent().getContent()).isEqualTo(commentContent+2);
	}

	@Test
	public void testPostCommentServiceFindAll() {
		// given
		when(postRepository.getReferenceById(postId)).thenReturn(post);

		PostComment postComment = new PostComment(post, user, commentContent);
		PostComment postCommentSub1 = new PostComment(post, user, commentContent+2);
		PostComment postCommentSub2 = new PostComment(post, user, commentContent+3);
		postCommentSub1.addChildComment(postCommentSub2);
		postComment.addChildComment(postCommentSub1);
		PostComment postComment2 = new PostComment(post, user, commentContent);
		List<PostComment> postComments = new ArrayList<>();
		postComments.add(postComment);
		postComments.add(postComment2);
		when(postCommentRepository.findAllByPostAndParentIsNull(any())).thenReturn(postComments);
		// when
		List<PostCommentResponseDto> findPostCommentResponseDtos = postCommentService.findAll(postId);

		// then
		assertThat(findPostCommentResponseDtos.get(0).getContent()).isEqualTo(commentContent);
		assertThat(findPostCommentResponseDtos.get(0).getChildList().get(0).getContent()).isEqualTo(commentContent+2);
		assertThat(findPostCommentResponseDtos.get(0).getChildList().get(0).getChildList().get(0).getContent()).isEqualTo(commentContent+3);
		assertThat(findPostCommentResponseDtos.get(1).getContent()).isEqualTo(commentContent);
	}		
	
	@Test
	public void testPostCommentServiceFindById() {
		// given
		PostComment postComment = new PostComment(post, user, commentContent);
		PostComment postComment2 = new PostComment(post, user, commentContent+2);
		PostComment postComment3 = new PostComment(post, user, commentContent+3);
		postComment2.addChildComment(postComment3);
		postComment.addChildComment(postComment2);

		Optional<PostComment> optinalPostComment = Optional.of(postComment);

		when(postCommentRepository.findById(any())).thenReturn(optinalPostComment);

		// when
		PostCommentResponseDto findPostCommentResponseDto = postCommentService.findById(postCommentId);

		// then
		assertThat(findPostCommentResponseDto.getContent()).isEqualTo(commentContent);
		assertThat(findPostCommentResponseDto.getChildList().get(0).getContent()).isEqualTo(commentContent+2);
		assertThat(findPostCommentResponseDto.getChildList().get(0).getChildList().get(0).getContent()).isEqualTo(commentContent+3);
	}

	@Test
	public void testPostCommentServiceUpdate() {
		// given
		PostComment postComment = new PostComment(post, user, commentContent);
		Optional<PostComment> optinalPostComment = Optional.of(postComment);
		when(postCommentRepository.findById(any())).thenReturn(optinalPostComment);

		String newContent = "update comment";
		PostCommentUpdateRequestDto requestDto = new PostCommentUpdateRequestDto(newContent);
		// when
		postCommentService.update(postCommentId, requestDto);

		// then
		assertThat(postComment.getContent()).isEqualTo(newContent);
	}

	@Test
	public void testPostCommentServiceDelete() {
		// given
		// when
		postCommentService.delete(postCommentId);
		// then
		verify(postCommentRepository).deleteById(postCommentId);

	}

}
