package com.marklog.blog.service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.marklog.blog.domain.notice.Notice;
import com.marklog.blog.domain.notice.NoticeRepository;
import com.marklog.blog.domain.notice.NoticeType;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;
import com.marklog.blog.dto.NoticeResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class NoticeService {
	private final UserRepository userRepository;
	private final NoticeRepository noticeRepository;

	public void save(Long userId, NoticeType noticeType, String content, String url) {
		User user = userRepository.getReferenceById(userId);
		Notice notice = new Notice(noticeType, content, url,  user);
		noticeRepository.save(notice);
	}

	public List<NoticeResponseDto> findAllByUserId(Long userId) {
		User user = userRepository.getReferenceById(userId);
		return noticeRepository.findAllByUser(user).stream().map(NoticeResponseDto::new).collect(Collectors.toList());
	}


	public void deleteAllNoticeByUserId(Long userId) {
		User user = userRepository.getReferenceById(userId);
		noticeRepository.deleteAllByUser(user);
	}
}
