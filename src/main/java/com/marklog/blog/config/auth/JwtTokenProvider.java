package com.marklog.blog.config.auth;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.marklog.blog.config.auth.dto.UserAuthenticationDto;
import com.marklog.blog.service.UserService;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

@Getter
@Component
public class JwtTokenProvider{
	private Key key;
	private Long accesstoken_expired;
	private Long refreshtoken_expired;

	public JwtTokenProvider(
			@Value("#{T(java.lang.Long).parseLong('${jwt.accesstoken_expired}')}")
			Long accesstoken_expired,
			@Value("#{T(java.lang.Long).parseLong('${jwt.refreshtoken_expired}')}")
			Long refreshtoken_expired
			){
		this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
		this.accesstoken_expired = accesstoken_expired;
		this.refreshtoken_expired = refreshtoken_expired;
	}

	public String createAccessToken(Long id, String email) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + accesstoken_expired);
		return Jwts.builder().setSubject(email).setId(id.toString()).setIssuer("marklog").setIssuedAt(now)
				.setExpiration(validity).signWith(key).compact();
	}

	public String createRefreshToken(Long id, String email) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + refreshtoken_expired);
		return Jwts.builder().setSubject(email).setId(id.toString()).setIssuer("marklog").setIssuedAt(now)
				.setExpiration(validity).signWith(key).compact();
	}

	public Boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException e) {
			return false;
		}
	}

	public Authentication getAuthentication(String token, UserService userService) {
		Long id = getId(token);
		try {
			UserAuthenticationDto userAuthenticationDto = userService.findAuthenticationDtoById(id);
			return new UsernamePasswordAuthenticationToken(userAuthenticationDto, null
					,Collections.singleton(new SimpleGrantedAuthority(userAuthenticationDto.getRole().getKey())));
		}catch(IllegalArgumentException e){
			return null;
		}

	}

	public String parseBearerToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public Long getId(String token) {
		return Long.parseLong(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getId());

	}

	public String getEmail(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();

	}
}
