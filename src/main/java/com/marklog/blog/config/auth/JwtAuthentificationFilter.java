package com.marklog.blog.config.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.marklog.blog.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtAuthentificationFilter extends OncePerRequestFilter {
	private final JwtTokenProvider jwtTokenProvider;
	private final UserService userService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = jwtTokenProvider.parseBearerToken(request);
	    if (token != null && jwtTokenProvider.validateToken(token)) {
	    	Authentication authentication = jwtTokenProvider.getAuthentication(token, userService);
	    	if(authentication != null) {
		    	SecurityContextHolder.getContext().setAuthentication(authentication);
	    	}
		}
		filterChain.doFilter(request, response);
	}
}