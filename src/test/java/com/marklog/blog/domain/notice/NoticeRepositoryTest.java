package com.marklog.blog.domain.notice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;

@Transactional
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class NoticeRepositoryTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	NoticeRepository noticeRepository;

	User user;

	@BeforeEach
	public void setUpEach() {
		String name = "name";
		String email = "test@gmail.com";
		String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String userTitle = "myblog";
		String introduce = "introduce";
		user = new User(name, email, picture, userTitle, introduce, Role.USER);
		user = userRepository.save(user);
	}

	public Notice createNotice() {
		String content = "content";
		String url="/post/1";
		Notice notice = new Notice(NoticeType.POST, content, url, user);
		noticeRepository.save(notice);
		return notice;
	}

	
	
	@Test
	public void testNoticeSave() {
		// given
		// when
		Notice notice = createNotice();

		// then
		assertThat(notice.getId()).isGreaterThan(0L);

	}
	
	@Test
	public void testNoticeFindAllByUser() {
		// given
		Notice notice1 = createNotice();
		Notice notice2 = createNotice();
		Notice notice3 = createNotice();

		// when
		List<Notice> notices = noticeRepository.findAllByUser(user);
		Notice findNotice1 = notices.get(0);
		Notice findNotice2 = notices.get(1);
		Notice findNotice3 = notices.get(2);

		// then
		assertThat(notices.size()).isEqualTo(3);
		assertThat(findNotice1.getId()).isEqualTo(notice1.getId());
		assertThat(findNotice2.getId()).isEqualTo(notice2.getId());
		assertThat(findNotice3.getId()).isEqualTo(notice3.getId());
	}

	@Test
	public void testNoticeFindAllByUser_user_not_exists() {
		// given
		createNotice();

		// when
		List<Notice> notices = noticeRepository.findAllByUser(null);

		// then
		assertThat(notices.isEmpty()).isTrue();
	}


	@Test
	public void testNoticeDeleteAllByUser() {
		// given
		createNotice();

		// when
		noticeRepository.deleteAllByUser(user);

		// then-ready
		List<Notice> notices = noticeRepository.findAllByUser(null);

		// then
		assertThat(notices.isEmpty()).isTrue();
	}
}
