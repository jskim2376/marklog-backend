package com.marklog.blog.controller;

import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marklog.blog.dto.PostListResponseDto;
import com.marklog.blog.service.SearchService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RequestMapping("/v1/search")
@RestController
public class SearchController {
	private final SearchService searchService;

	@GetMapping
	public ResponseEntity<Page<PostListResponseDto>> search(Pageable pageable, @RequestParam("keyword") String keyword) {
		try {
			String[] keywords = keyword.split(" ");
			Page<PostListResponseDto> result = searchService.search(pageable, keywords);
			return ResponseEntity.ok(result);
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/user")
	public ResponseEntity<Page<PostListResponseDto>> search(Pageable pageable, @RequestParam("userId") Long userId) {
		try {
			Page<PostListResponseDto> result = searchService.searchByUserId(pageable, userId);
			return ResponseEntity.ok(result);
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}

	
	@GetMapping("/tag")
	public ResponseEntity<Page<PostListResponseDto>> searchByTag(Pageable pageable, @RequestParam("tag") String tag) {
		try {
			Page<PostListResponseDto> result = searchService.searchByTag(pageable, tag);
			return ResponseEntity.ok(result);
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}

	
	
}
