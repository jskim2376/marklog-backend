package com.marklog.blog.config.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.service.UserService;

@ExtendWith(MockitoExtension.class)
public class JwtOAuth2UserServiceTest {
	@Mock
	UserService userService;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	OAuth2UserRequest oAuth2UserRequest;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

	@Mock
	OAuth2User oauth2User;

	@Mock
	User user;

	@Test
	public void testJwtOAuth2UserService() {
		// given
		String myUserNameAttributeKey = "id";
		Long id = 1L;
		HashMap<String, Object> attributes = new HashMap<>();
		attributes.put("email", "test@gmail.com");
		attributes.put(myUserNameAttributeKey, id);

		when(oAuth2UserRequest.getClientRegistration().getRegistrationId()).thenReturn("google");
		when(oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
				.getUserNameAttributeName()).thenReturn(myUserNameAttributeKey);
		when(delegate.loadUser(any()).getAttributes()).thenReturn(attributes);
		when(userService.saveOrUpdate(any())).thenReturn(user);
		when(user.getRoleKey()).thenReturn(Role.USER.getKey());
		when(user.toAttributes()).thenReturn(attributes);

		// when
		JwtOAuth2UserService jwtOAuth2UserService = new JwtOAuth2UserService(userService);
		jwtOAuth2UserService.setDelegate(delegate);
		OAuth2User returnOAuth2User = jwtOAuth2UserService.loadUser(oAuth2UserRequest);

		boolean hasRole = returnOAuth2User.getAuthorities().contains(new SimpleGrantedAuthority(Role.USER.getKey()));
		Map<String, Object> getattr = returnOAuth2User.getAttributes();
		Long getId = Long.valueOf(returnOAuth2User.getName());

		// then
		assertThat(hasRole).isTrue();
		assertThat(getattr).isEqualTo(user.toAttributes());
		assertThat(getId).isEqualTo(id);
	}

}