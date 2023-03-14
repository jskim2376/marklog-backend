package com.marklog.blog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.marklog.blog.dto.TagCountResponseDto;
import com.marklog.blog.service.TagService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/v1/tag")
@Controller
public class TagController {
	private final TagService tagService;

	@GetMapping("/{userId}")
	public ResponseEntity<List<TagCountResponseDto>> getCountTagNameByUserId(@PathVariable Long userId) {
		return ResponseEntity.ok(tagService.countTagNameByUserId(userId));
	}
}
