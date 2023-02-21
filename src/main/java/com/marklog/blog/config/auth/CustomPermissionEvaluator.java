package com.marklog.blog.config.auth;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.marklog.blog.config.auth.dto.UserAuthenticationDto;
import com.marklog.blog.controller.dto.PostResponseDto;
import com.marklog.blog.domain.post.comment.PostComment;
import com.marklog.blog.service.PostCommentService;
import com.marklog.blog.service.PostService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
	private final PostService postService;
	private final PostCommentService postCommentService;

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission) {
		// TODO Auto-generated method stub
		if (authentication != null) {
			if (targetType.equals("post")) {
				Long postId = (Long) targetId;
				PostResponseDto postResponseDto = postService.findById(postId);
				Long postUserId = postResponseDto.getUserId();
				Long authUserId = ((UserAuthenticationDto) authentication.getPrincipal()).getId();
				if (postUserId == authUserId) {
					return true;
				}
			}
			else if(targetType.equals("postComment")){
				Long postCommentId = (Long) targetId;
				PostComment postComment = postCommentService.findById(postCommentId);
				Long postCommentUserId = postComment.getUser().getId();
				Long authUserId = ((UserAuthenticationDto) authentication.getPrincipal()).getId();
				if (postCommentUserId == authUserId) {
					return true;
				}
				
			}
		}
		return false;
	}
}