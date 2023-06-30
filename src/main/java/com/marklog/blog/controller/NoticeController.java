package com.marklog.blog.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marklog.blog.dto.NoticeResponseDto;
import com.marklog.blog.service.NoticeService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/v1/user/{userId}/notice")
@RestController
public class NoticeController {
	private final NoticeService noticeService;

	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or principal.id==#userId)")
	@GetMapping
	public ResponseEntity<List<NoticeResponseDto>> getAllNoticeByUserId(@PathVariable Long userId) {
		List<NoticeResponseDto> notices = noticeService.findAllByUserId(userId);
		return ResponseEntity.ok(notices);
	}

	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or principal.id==#userId)")
	@DeleteMapping
	public ResponseEntity<?> deleteAllNotice(@PathVariable Long userId) {
		noticeService.deleteAllNoticeByUserId(userId);
		return ResponseEntity.noContent().build();
	}


}
