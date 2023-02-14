package com.marklog.blog.config.auth;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.marklog.blog.config.auth.dto.OAuthAttributes;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.service.UserService;

import lombok.Setter;

@Setter
@Service
public class JwtOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>{


	private final UserService userService;
	private OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

	public JwtOAuth2UserService(UserService userService) {
		super();
		this.userService = userService;
		this.delegate = new DefaultOAuth2UserService();
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		String registratrionId = userRequest.getClientRegistration().getRegistrationId();
		String userNameAttributeKey = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
		Map<String, Object> attributes = delegate.loadUser(userRequest).getAttributes();
		OAuthAttributes oAuthAttributes = OAuthAttributes.of(registratrionId, userNameAttributeKey, attributes);

        User user = userService.saveOrUpdate(oAuthAttributes);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                user.toAttributes(),
                "id"
        );
    }

}
