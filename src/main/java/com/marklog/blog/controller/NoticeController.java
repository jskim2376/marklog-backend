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

import com.marklog.blog.controller.dto.NoticeResponseDto;
import com.marklog.blog.service.NoticeService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/v1/notice")
@RestController
public class NoticeController {
	private final NoticeService noticeService;
	
	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or principal.id==#userId)")
	@GetMapping("/{userId}/uncheck")
	public ResponseEntity<List<NoticeResponseDto>> getAllUnCheckNotice(@PathVariable Long userId) {
		List<NoticeResponseDto> notices = noticeService.findAllUnCheckNotice(userId);
		return ResponseEntity.ok(notices);
	}
	
	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasPermission(#id, 'notice', null))")
	@PutMapping("/{id}")
	public ResponseEntity<?> checkNotice(@PathVariable Long id) {
		try {
			noticeService.checkNoticeById(id);
			return ResponseEntity.noContent().build();
		}catch(NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasPermission(#id, 'notice', null))")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteNotice(@PathVariable Long id) {
		try {
			noticeService.deleteNotice(id);
			return ResponseEntity.noContent().build();
		}catch(EmptyResultDataAccessException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	
}
