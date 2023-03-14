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
	public void testFindAllUnCheckNotice() {
		// given
		List<Notice> notices = new ArrayList<>();
		Notice notice = new Notice(noticeContent, user);
		notices.add(notice);

		when(userRepository.getReferenceById(userId)).thenReturn(user);
		when(noticeRepository.findAllByUserAndCheckFlagFalse(user)).thenReturn(notices);

		// when
		List<NoticeResponseDto> findNotices = noticeService.findAllUnCheckNotice(userId);

		//then
		assertThat(notices.get(0).getContent()).isSameAs(findNotices.get(0).getContent());
		assertThat(notices.get(0).getCheckFlag()).isSameAs(findNotices.get(0).getCheckFlag());
		assertThat(notices.get(0).getUser().getId()).isSameAs(findNotices.get(0).getUserId());
	}

	@Test
	public void testFindById() {
		// given
		Notice notice = new Notice(noticeContent, user);
		Optional<Notice> optinal = Optional.of(notice);
		when(noticeRepository.findById(noticeId)).thenReturn(optinal);

		// when
		NoticeResponseDto noticeResponseDto = noticeService.findById(noticeId);

		//then
		assertThat(noticeResponseDto.getContent()).isEqualTo(notice.getContent());
		assertThat(noticeResponseDto.getCheckFlag()).isSameAs(false);

	}

	@Test
	public void testPushNoticeByUserId() {
		//given
		when(userRepository.getReferenceById(userId)).thenReturn(user);

		//when
		noticeService.pushNoticeByUserId(noticeContent, userId);

		//then
		verify(noticeRepository).save(any(Notice.class));
	}

	@Test
	public void testCheckNoticeByid() {
		//given
		Optional<Notice> notice = Optional.of(new Notice(noticeContent, user));
		when(noticeRepository.findById(noticeId)).thenReturn(notice);

		//when
		noticeService.checkNoticeById(noticeId);

		//then
		assertThat(notice.get().getCheckFlag()).isTrue();
	}

	@Test
	public void testDeleteNotice() {
		//given
		//then
		noticeService.deleteNotice(noticeId);

		//then
		verify(noticeRepository).deleteById(noticeId);
	}
}
