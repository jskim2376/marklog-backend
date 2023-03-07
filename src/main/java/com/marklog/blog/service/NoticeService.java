package com.marklog.blog.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.marklog.blog.controller.dto.NoticeResponseDto;
import com.marklog.blog.domain.notice.Notice;
import com.marklog.blog.domain.notice.NoticeRepository;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class NoticeService {
	private final UserRepository userRepository;
	private final NoticeRepository noticeRepository;

	public List<NoticeResponseDto> findAllUnCheckNotice(Long userId) {
		User user = userRepository.getReferenceById(userId);
		List<Notice> notices = noticeRepository.findAllByUserAndCheckFlagFalse(user);
		return notices.stream().map(NoticeResponseDto::new).collect(Collectors.toList());
	}

	public NoticeResponseDto findById(Long id) {
		Notice notice = noticeRepository.findById(id).orElseThrow();
		return new NoticeResponseDto(notice);
	}

	
	public void pushNoticeByUserId(String content, Long userId) {
		User user = userRepository.getReferenceById(userId);
		Notice notice = new Notice(content, user);
		noticeRepository.save(notice);
	}

	public void checkNoticeById(Long id) throws NoSuchElementException {
		Notice notice = noticeRepository.findById(id).orElseThrow();
		notice.setCheckFlag(true);
	}

	public void deleteNotice(Long id) {
		noticeRepository.deleteById(id);
	}
}
