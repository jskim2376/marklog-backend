package com.marklog.blog.config;

import java.util.Collections;
import java.util.HashMap;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithOauth2UserSecurityContextFactory implements WithSecurityContextFactory<WithOauth2User>{

	@Override
	public SecurityContext createSecurityContext(WithOauth2User annotaion) {


        SecurityContext context = SecurityContextHolder.createEmptyContext();
//        httpSession.setAttribute("user", new SessionUser(user));
        OAuth2User oauth2User =  new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(annotaion.role().getKey())),
                new HashMap<String, Object>(),
                "google"
        );

        OAuth2AuthenticationToken oAuth2AuthenticationToken = new OAuth2AuthenticationToken(oauth2User, oauth2User.getAuthorities(), "google");
        context.setAuthentication(oAuth2AuthenticationToken);
        return context;
	}

}
