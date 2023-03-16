package com.marklog.blog.domain.notice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NoticeType {
	POST("NOTICE_POST"), COMMENT("NOTICE_COMMENT"), ANNOUNCEMENT("NOTICE_ANNOUNCEMENT");
	private final String key;
}