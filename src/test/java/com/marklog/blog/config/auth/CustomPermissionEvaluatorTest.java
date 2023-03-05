package com.marklog.blog.config.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.marklog.blog.config.auth.dto.UserAuthenticationDto;
import com.marklog.blog.controller.dto.PostCommentResponseDto;
import com.marklog.blog.controller.dto.PostResponseDto;
import com.marklog.blog.service.PostCommentService;
import com.marklog.blog.service.PostService;

@ExtendWith(MockitoExtension.class)
public class CustomPermissionEvaluatorTest {

	@Test
	public void hasPermission_with_Post() {
		// given
		PostService postService = mock(PostService.class);
		PostCommentService postCommentService = mock(PostCommentService.class);
		CustomPermissionEvaluator customPermissionEvaluator = new CustomPermissionEvaluator(postService, postCommentService);
		
		String targetType = "post";
		Long targetId = 1L;
		PostResponseDto postResponseDto = mock(PostResponseDto.class);
		when(postService.findById(targetId)).thenReturn(postResponseDto);
		Long postUserId = 10L;
		when(postResponseDto.getUserId()).thenReturn(postUserId);
		
		Authentication authentication = mock(Authentication.class);
		UserAuthenticationDto userAuthenticationDto = mock(UserAuthenticationDto.class);
		when(authentication.getPrincipal()).thenReturn(userAuthenticationDto);
		Long authUserId = postUserId;
		when(userAuthenticationDto.getId()).thenReturn(authUserId);
		
		//when
		Boolean haspermissionResult = customPermissionEvaluator.hasPermission(authentication, targetId, targetType, null);
		
		//then
		assertThat(haspermissionResult).isTrue();
	}
	
	@Test
	public void hasPermission_with_Post_not_found() {
		// given
		PostService postService = mock(PostService.class);
		PostCommentService postCommentService = mock(PostCommentService.class);
		CustomPermissionEvaluator customPermissionEvaluator = new CustomPermissionEvaluator(postService, postCommentService);
		
		String targetType = "post";
		Long targetId = 1L;
		when(postService.findById(targetId)).thenThrow(new NoSuchElementException());
		
		//when
		Boolean haspermissionResult = customPermissionEvaluator.hasPermission(mock(Authentication.class), targetId, targetType, null);
		
		//then
		assertThat(haspermissionResult).isFalse();
	}

	@Test
	public void hasPermission_with_Post_not_match_authuserId_and_postUserId() {
		// given
		PostService postService = mock(PostService.class);
		PostCommentService postCommentService = mock(PostCommentService.class);
		CustomPermissionEvaluator customPermissionEvaluator = new CustomPermissionEvaluator(postService, postCommentService);
		
		String targetType = "post";
		Long targetId = 1L;
		PostResponseDto postResponseDto = mock(PostResponseDto.class);
		when(postService.findById(targetId)).thenReturn(postResponseDto);
		Long postUserId = 10L;
		when(postResponseDto.getUserId()).thenReturn(postUserId);
		
		Authentication authentication = mock(Authentication.class);
		UserAuthenticationDto userAuthenticationDto = mock(UserAuthenticationDto.class);
		when(authentication.getPrincipal()).thenReturn(userAuthenticationDto);
		Long authUserId = 2L;
		when(userAuthenticationDto.getId()).thenReturn(authUserId);
		
		//when
		Boolean haspermissionResult = customPermissionEvaluator.hasPermission(authentication, targetId, targetType, null);
		
		//then
		assertThat(haspermissionResult).isFalse();
	}

	
	@Test
	public void hasPermission_with_PostComment() {
		// given
		PostService postService = mock(PostService.class);
		PostCommentService postCommentService = mock(PostCommentService.class);
		CustomPermissionEvaluator customPermissionEvaluator = new CustomPermissionEvaluator(postService, postCommentService);
		
		String targetType = "postComment";
		Long targetId = 1L;
		PostCommentResponseDto postCommentResponseDto = mock(PostCommentResponseDto.class);
		when(postCommentService.findById(targetId)).thenReturn(postCommentResponseDto);
		Long postUserId = 10L;
		when(postCommentResponseDto.getUserId()).thenReturn(postUserId);
		
		Authentication authentication = mock(Authentication.class);
		UserAuthenticationDto userAuthenticationDto = mock(UserAuthenticationDto.class);
		when(authentication.getPrincipal()).thenReturn(userAuthenticationDto);
		Long authUserId = postUserId;
		when(userAuthenticationDto.getId()).thenReturn(authUserId);
		
		//when
		Boolean haspermissionResult = customPermissionEvaluator.hasPermission(authentication, targetId, targetType, null);
		
		//then
		assertThat(haspermissionResult).isTrue();
	}
	
	@Test
	public void hasPermission_with_PostComment_not_found() {
		// given
		PostService postService = mock(PostService.class);
		PostCommentService postCommentService = mock(PostCommentService.class);
		CustomPermissionEvaluator customPermissionEvaluator = new CustomPermissionEvaluator(postService, postCommentService);
		
		String targetType = "postComment";
		Long targetId = 1L;
		when(postCommentService.findById(targetId)).thenThrow(new NoSuchElementException());
		
		//when
		Boolean haspermissionResult = customPermissionEvaluator.hasPermission(mock(Authentication.class), targetId, targetType, null);
		
		//then
		assertThat(haspermissionResult).isFalse();
	}

	@Test
	public void hasPermission_with_PostComment_not_match_authuserId_and_postUserId() {
		// given
		PostService postService = mock(PostService.class);
		PostCommentService postCommentService = mock(PostCommentService.class);
		CustomPermissionEvaluator customPermissionEvaluator = new CustomPermissionEvaluator(postService, postCommentService);
		
		String targetType = "postComment";
		Long targetId = 1L;
		PostCommentResponseDto postCommentResponseDto = mock(PostCommentResponseDto.class);
		when(postCommentService.findById(targetId)).thenReturn(postCommentResponseDto);
		Long postUserId = 10L;
		when(postCommentResponseDto.getUserId()).thenReturn(postUserId);
		
		Authentication authentication = mock(Authentication.class);
		UserAuthenticationDto userAuthenticationDto = mock(UserAuthenticationDto.class);
		when(authentication.getPrincipal()).thenReturn(userAuthenticationDto);
		Long authUserId = 2L;
		when(userAuthenticationDto.getId()).thenReturn(authUserId);
		
		//when
		Boolean haspermissionResult = customPermissionEvaluator.hasPermission(authentication, targetId, targetType, null);
		
		//then
		assertThat(haspermissionResult).isFalse();
	}
	
	
	
	
	
	
	
	
}
