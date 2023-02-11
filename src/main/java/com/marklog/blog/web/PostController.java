package com.marklog.blog.web;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.marklog.blog.config.auth.LoginUser;
import com.marklog.blog.config.auth.dto.SessionUser;
import com.marklog.blog.service.PostService;
import com.marklog.blog.web.dto.PostResponseDto;
import com.marklog.blog.web.dto.PostSaveRequestDto;
import com.marklog.blog.web.dto.PostUpdateRequestDto;
import com.nimbusds.jose.shaded.json.JSONObject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class PostController {
	private final PostService postService;

	@ResponseStatus(value = HttpStatus.CREATED)
	@PreAuthorize("isAuthenticated()")
@PostMapping("/post")
	public JSONObject save(@RequestBody PostSaveRequestDto requestDto) {
		JSONObject obj = new JSONObject();

		obj.put("id",postService.save(requestDto));
		return obj;
	}


	@GetMapping("/post/{id}")
	public PostResponseDto findById(@PathVariable Long id) {
		return postService.findById(id);
	}

	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or #id == #user.getId())")
	@PutMapping("/post/{id}")
	public JSONObject update(@PathVariable Long id, @RequestBody PostUpdateRequestDto requestDto, @LoginUser SessionUser user) {
		JSONObject obj = new JSONObject();
		obj.put("id", postService.update(id, requestDto));
		return obj;
	}
	
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or #id == #user.getId())")
	@DeleteMapping("/post/{id}")
	public void userDelete(@PathVariable Long id, @LoginUser SessionUser user) {
		postService.delete(id);
	}
}
