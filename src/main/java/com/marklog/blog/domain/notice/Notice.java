package com.marklog.blog.domain.notice;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;

import com.marklog.blog.domain.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Notice {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NoticeType noticeType;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;
	
	@Column
	private String url;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	public Notice(NoticeType noticeType, String content, String url, User user) {
		this.noticeType = noticeType;
		this.content = content;
		this.url = url;
		this.user = user;
	}
	
	
}
