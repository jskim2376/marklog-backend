package com.marklog.blog.config;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import org.springframework.security.test.context.support.WithSecurityContext;

import com.marklog.blog.domain.user.Role;

@Retention(RUNTIME)
@WithSecurityContext(factory = WithOauth2UserSecurityContextFactory.class)
public @interface WithOauth2User {
	String name() default "name";
	String email() default "email@gmail.com";
	String picture() default "https://naver.com";
	String title() default "title";
	String introducee() default "introduce";
	Role role() default Role.USER;


}
