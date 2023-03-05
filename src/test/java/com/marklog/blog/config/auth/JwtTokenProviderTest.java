package com.marklog.blog.config.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.marklog.blog.config.auth.dto.UserAuthenticationDto;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.service.UserService;

import io.jsonwebtoken.Jwts;

@ExtendWith(MockitoExtension.class)
public class JwtTokenProviderTest {
	@Mock
	UserService userService;

	@Mock
	HttpServletRequest request;

	Long id;
	String email;
	JwtTokenProvider jwtTokenProvider;

	@BeforeEach
	public void setUp() {
		id = 1L;
		email = "test@test.com";
		jwtTokenProvider = new JwtTokenProvider(1000000L, 1000000L);
	}

	@Test
	public void testCreateAccessToken() {
		// given
		// when
		String accessToken = jwtTokenProvider.createAccessToken(id, email);
		// then
		assertThat(Long.parseLong(Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getKey()).build()
				.parseClaimsJws(accessToken).getBody().getId())).isSameAs(id);
		assertThat(Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getKey()).build().parseClaimsJws(accessToken)
				.getBody().getSubject()).isEqualTo(email);
	}

	@Test
	public void testCreateRefreshToken() {
		// given
		// when
		String refreshToken = jwtTokenProvider.createRefreshToken(id, email);
		// then
		assertThat(Long.parseLong(Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getKey()).build()
				.parseClaimsJws(refreshToken).getBody().getId())).isSameAs(id);
		assertThat(Jwts.parserBuilder().setSigningKey(jwtTokenProvider.getKey()).build().parseClaimsJws(refreshToken)
				.getBody().getSubject()).isEqualTo(email);
	}

	@Test
	public void testValidToken() {
		// given
		String accessToken = jwtTokenProvider.createAccessToken(id, email);
		// when
		Boolean validTokenTrue = jwtTokenProvider.validateToken(accessToken);
		Boolean validTokenFalse = jwtTokenProvider.validateToken(accessToken + "error");

		// then
		assertThat(validTokenTrue).isEqualTo(true);
		assertThat(validTokenFalse).isEqualTo(false);
	}

	@Test
	public void testGetAuthentication() {
		// given
		String accessToken = jwtTokenProvider.createAccessToken(id, email);
		UserAuthenticationDto userAuthenticationDto = new UserAuthenticationDto(id, accessToken, Role.USER);
		when(userService.findAuthenticationDtoById(id)).thenReturn(userAuthenticationDto);

		// when
		Authentication authentication = jwtTokenProvider.getAuthentication(accessToken, userService);

		// then

		UserAuthenticationDto principal = (UserAuthenticationDto) authentication.getPrincipal();
		assertThat(principal).isEqualTo(userAuthenticationDto);

		assertThat(authentication.getCredentials()).isNull();

		boolean hasRole = authentication.getAuthorities()
				.contains(new SimpleGrantedAuthority(principal.getRole().getKey()));
		assertThat(hasRole).isEqualTo(true);
	}

	@Test
	public void testParseBearerToken() {
		// given
		String token = "token";
		String bearerToken = "Bearer " + token;
		when(request.getHeader("Authorization")).thenReturn(bearerToken);
		// when
		String parseBearerToken = jwtTokenProvider.parseBearerToken(request);
		// then
		assertThat(parseBearerToken).isEqualTo(token);

	}

	@Test
	public void testGetId() {
		// given
		String accessToken = jwtTokenProvider.createAccessToken(id, email);
		// then
		Long getId = jwtTokenProvider.getId(accessToken);
		// when
		assertThat(getId).isEqualTo(id);
	}

	@Test
	public void testGetEmail() {
		// given
		String accessToken = jwtTokenProvider.createAccessToken(id, email);
		// then
		String getEmail = jwtTokenProvider.getEmail(accessToken);
		// when
		assertThat(getEmail).isEqualTo(email);
	}
}
