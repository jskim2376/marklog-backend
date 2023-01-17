package com.marklog.blog.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("ROLE_ADMIN", "일반유저"),
    USER("ROLE_USER", "관리자");

    private final String key;
    private final String title;
}