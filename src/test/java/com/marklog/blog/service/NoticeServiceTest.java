package com.marklog.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marklog.blog.domain.notice.Notice;
import com.marklog.blog.domain.notice.NoticeRepository;
import com.marklog.blog.domain.notice.NoticeType;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;
import com.marklog.blog.dto.NoticeResponseDto;

@ExtendWith(MockitoExtension.class)
public class NoticeServiceTest {
	@Mock
	UserRepository userRepository;

	@Mock
	NoticeRepository noticeRepository;

	User user;
	Long userId = 1L;

	NoticeService noticeService;
	Long noticeId = 2L;
	String noticeContent = "content";

	@BeforeEach
	public void setup() {
		noticeService = new NoticeService(userRepository, noticeRepository);
		String name = "name";
		String email = "test@gmail.com";
		String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String userTitle = "myblog";
		String introduce = "introduce";
		user = new User(name, email, picture, userTitle, introduce, Role.USER);
	}

	@Test
	public void testNoticeServiceSave() {
	//given
	when(userRepository.getReferenceById(userId)).thenReturn(user);
	String content = "content";
	String url = "/post/1";

	//when
	noticeService.save(userId, NoticeType.POST, content, url);

	//then
	verify(noticeRepository).save(any(Notice.class));
}

	@Test
	public void testNoticeServiceFindAll() {
		// given
		String content = "content";
		String url = "/post/1";
		Notice notice = new Notice(NoticeType.POST, content, url, user);

		List<Notice> notices = new ArrayList<>();
		notices.add(notice);

		when(userRepository.getReferenceById(userId)).thenReturn(user);
		when(noticeRepository.findAllByUser(user)).thenReturn(notices);

		// when
		List<NoticeResponseDto> findNotices = noticeService.findAllByUserId(userId);

		// then
		assertThat(notices.get(0).getContent()).isSameAs(findNotices.get(0).getContent());
		assertThat(notices.get(0).getNoticeType()).isSameAs(findNotices.get(0).getNoticeType());
		assertThat(notices.get(0).getUrl()).isSameAs(findNotices.get(0).getUrl());
		assertThat(findNotices.get(0).getUserId()).isNull();
	}

	@Test
	public void testNoticeServiceDeleteAll() {
		// given
		when(userRepository.getReferenceById(userId)).thenReturn(user);
		
		// then
		noticeService.deleteAllNoticeByUserId(userId);

		// then
		verify(noticeRepository).deleteAllByUser(user);
	}
}
