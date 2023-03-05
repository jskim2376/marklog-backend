package com.marklog.blog.config.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	private final JwtOAuth2UserService jwtOAuth2UserService;
	private final JwtOAuth2LoginSuccessHandler jwtOAuth2LoginSuccessHandler;
	private final JwtAuthentificationFilter jwtAuthentificationFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final CustomPermissionEvaluator customPermissionEvaluator;

	@Bean
	public RoleHierarchy roleHierarchy() {
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
		return roleHierarchy;
	}

	@Bean
	public SecurityFilterChain userChain(HttpSecurity http) throws Exception {
		http.formLogin().disable().httpBasic().disable().csrf().disable().headers().frameOptions().disable().and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.exceptionHandling(
						exceptionHandling -> exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint))
				.oauth2Login(oauth2Login -> oauth2Login.successHandler(jwtOAuth2LoginSuccessHandler).userInfoEndpoint()
						.userService(jwtOAuth2UserService))
				.addFilterBefore(jwtAuthentificationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	//
	@Bean
	public MethodSecurityExpressionHandler createExpressionHandler() {
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setPermissionEvaluator(customPermissionEvaluator);
		return expressionHandler;
	}

}
