package com.marklog.blog.controller.dto;

import com.marklog.blog.domain.notice.Notice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResponseDto {
	private Long id;
	private String content;
	private Boolean checkFlag = false;
	private Long userId;
	public NoticeResponseDto(Notice notice) {
		super();
		this.id = notice.getId();
		this.content = notice.getContent();
		this.checkFlag = notice.getCheckFlag();
		this.userId = notice.getUser().getId();
	}
	
}
