package com.marklog.blog.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("ROLE_ADMIN", "������"),
    USER("ROLE_USER", "�Ϲ� �����");

    private final String key;
    private final String title;
}