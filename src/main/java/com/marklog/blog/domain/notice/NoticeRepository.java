package com.marklog.blog.domain.notice;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marklog.blog.domain.user.User;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
	List<Notice> findAllByUser(User user);
	void deleteAllByUser(User user);
}
