package com.marklog.blog.dto;

import com.marklog.blog.domain.notice.Notice;
import com.marklog.blog.domain.notice.NoticeType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResponseDto {
	private NoticeType noticeType;
	private String content;
	private String url;
	private Long userId;
	
	public NoticeResponseDto(Notice notice) {
		super();
		this.noticeType = notice.getNoticeType();
		this.content = notice.getContent();
		this.url = notice.getUrl();
		this.userId = notice.getUser().getId();
	}

}
