package com.marklog.blog.config.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.marklog.blog.config.auth.dto.UserAuthenticationDto;
import com.marklog.blog.dto.NoticeResponseDto;
import com.marklog.blog.dto.PostCommentResponseDto;
import com.marklog.blog.dto.PostResponseDto;
import com.marklog.blog.service.NoticeService;
import com.marklog.blog.service.PostCommentService;
import com.marklog.blog.service.PostService;

@ExtendWith(MockitoExtension.class)
public class CustomPermissionEvaluatorTest {
	PostService postService;
	PostCommentService postCommentService;
	NoticeService noticeService;
	CustomPermissionEvaluator customPermissionEvaluator;

	@BeforeEach
	public void setup() {
		postService = mock(PostService.class);
		postCommentService = mock(PostCommentService.class);
		noticeService = mock(NoticeService.class);
		customPermissionEvaluator = new CustomPermissionEvaluator(postService, postCommentService, noticeService);
	}

	@Test
	public void hasPermission_with_Post() {
		// given
		String targetType = "post";
		Long targetId = 1L;
		PostResponseDto postResponseDto = mock(PostResponseDto.class);
		Long postUserId = 10L;
		Authentication authentication = mock(Authentication.class);
		UserAuthenticationDto userAuthenticationDto = mock(UserAuthenticationDto.class);
		Long authUserId = postUserId;

		when(postService.findById(targetId)).thenReturn(postResponseDto);
		when(postResponseDto.getUserId()).thenReturn(postUserId);
		when(authentication.getPrincipal()).thenReturn(userAuthenticationDto);
		when(userAuthenticationDto.getId()).thenReturn(authUserId);

		// when
		Boolean haspermissionResult = customPermissionEvaluator.hasPermission(authentication, targetId, targetType,
				null);

		// then
		assertThat(haspermissionResult).isTrue();
	}

	@Test
	public void hasPermission_with_Post_not_found() {
		// given
		String targetType = "post";
		Long targetId = 1L;
		when(postService.findById(targetId)).thenThrow(new NoSuchElementException());

		// when
		Boolean haspermissionResult = customPermissionEvaluator.hasPermission(mock(Authentication.class), targetId,
				targetType, null);

		// then
		assertThat(haspermissionResult).isFalse();
	}

	@Test
	public void hasPermission_with_Post_not_match_authuserId_and_postUserId() {
		// given
		String targetType = "post";
		Long targetId = 1L;
		PostResponseDto postResponseDto = mock(PostResponseDto.class);
		Long postUserId = 10L;
		Authentication authentication = mock(Authentication.class);
		UserAuthenticationDto userAuthenticationDto = mock(UserAuthenticationDto.class);
		Long authUserId = postUserId + 1;

		when(postService.findById(targetId)).thenReturn(postResponseDto);
		when(postResponseDto.getUserId()).thenReturn(postUserId);
		when(authentication.getPrincipal()).thenReturn(userAuthenticationDto);
		when(userAuthenticationDto.getId()).thenReturn(authUserId);

		// when
		Boolean haspermissionResult = customPermissionEvaluator.hasPermission(authentication, targetId, targetType,
				null);

		// then
		assertThat(haspermissionResult).isFalse();
	}

	@Test
	public void hasPermission_with_PostComment() {
		// given
		String targetType = "postComment";
		Long targetId = 1L;
		PostCommentResponseDto postCommentResponseDto = mock(PostCommentResponseDto.class);
		Long postUserId = 10L;

		Authentication authentication = mock(Authentication.class);
		UserAuthenticationDto userAuthenticationDto = mock(UserAuthenticationDto.class);
		Long authUserId = postUserId;

		when(postCommentService.findById(targetId)).thenReturn(postCommentResponseDto);
		when(postCommentResponseDto.getUserId()).thenReturn(postUserId);
		when(authentication.getPrincipal()).thenReturn(userAuthenticationDto);
		when(userAuthenticationDto.getId()).thenReturn(authUserId);

		// when
		Boolean haspermissionResult = customPermissionEvaluator.hasPermission(authentication, targetId, targetType,
				null);

		// then
		assertThat(haspermissionResult).isTrue();
	}

	@Test
	public void hasPermission_with_PostComment_not_found() {
		// given
		String targetType = "postComment";
		Long targetId = 1L;
		when(postCommentService.findById(targetId)).thenThrow(new NoSuchElementException());

		// when
		Boolean haspermissionResult = customPermissionEvaluator.hasPermission(mock(Authentication.class), targetId,
				targetType, null);

		// then
		assertThat(haspermissionResult).isFalse();
	}

	@Test
	public void hasPermission_with_PostComment_not_match_authuserId_and_postUserId() {
		// given
		String targetType = "postComment";
		Long targetId = 1L;
		PostCommentResponseDto postCommentResponseDto = mock(PostCommentResponseDto.class);
		Long postUserId = 10L;

		Authentication authentication = mock(Authentication.class);
		UserAuthenticationDto userAuthenticationDto = mock(UserAuthenticationDto.class);
		Long authUserId = postUserId + 1;

		when(postCommentService.findById(targetId)).thenReturn(postCommentResponseDto);
		when(postCommentResponseDto.getUserId()).thenReturn(postUserId);
		when(authentication.getPrincipal()).thenReturn(userAuthenticationDto);
		when(userAuthenticationDto.getId()).thenReturn(authUserId);

		// when
		Boolean haspermissionResult = customPermissionEvaluator.hasPermission(authentication, targetId, targetType,
				null);

		// then
		assertThat(haspermissionResult).isFalse();
	}

	@Test
	public void hasPermission_with_Notice() {
		// given
		String targetType = "notice";
		Long targetId = 1L;
		NoticeResponseDto noticeResponseDto = mock(NoticeResponseDto.class);
		Long noticeId = 10L;

		Authentication authentication = mock(Authentication.class);
		UserAuthenticationDto userAuthenticationDto = mock(UserAuthenticationDto.class);
		Long authUserId = noticeId;

		when(noticeService.findById(targetId)).thenReturn(noticeResponseDto);
		when(noticeResponseDto.getUserId()).thenReturn(noticeId);
		when(authentication.getPrincipal()).thenReturn(userAuthenticationDto);
		when(userAuthenticationDto.getId()).thenReturn(authUserId);

		// when
		Boolean haspermissionResult = customPermissionEvaluator.hasPermission(authentication, targetId, targetType,
				null);

		// then
		assertThat(haspermissionResult).isTrue();
	}

	@Test
	public void hasPermission_with_Notice_not_found() {
		// given
		String targetType = "notice";
		Long targetId = 1L;
		when(noticeService.findById(targetId)).thenThrow(new NoSuchElementException());

		// when
		Boolean haspermissionResult = customPermissionEvaluator.hasPermission(mock(Authentication.class), targetId,
				targetType, null);

		// then
		assertThat(haspermissionResult).isFalse();

	}

	@Test
	public void hasPermission_with_Notice_not_match_authuserId_and_postUserId() {
		String targetType = "notice";
		Long targetId = 1L;
		NoticeResponseDto noticeResponseDto = mock(NoticeResponseDto.class);
		Long noticeId = 10L;

		Authentication authentication = mock(Authentication.class);
		UserAuthenticationDto userAuthenticationDto = mock(UserAuthenticationDto.class);
		Long authUserId = noticeId + 1;

		when(noticeService.findById(targetId)).thenReturn(noticeResponseDto);
		when(noticeResponseDto.getUserId()).thenReturn(noticeId);
		when(authentication.getPrincipal()).thenReturn(userAuthenticationDto);
		when(userAuthenticationDto.getId()).thenReturn(authUserId);

		// when
		Boolean haspermissionResult = customPermissionEvaluator.hasPermission(authentication, targetId, targetType,
				null);

		// then
		assertThat(haspermissionResult).isFalse();
	}

}
