package com.marklog.blog.config.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig{
	private final CustomOAuth2UserService customOAuth2UserService;


	@Bean
	public RoleHierarchy roleHierarchy() {
	    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
	    roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
	    return roleHierarchy;
	}

	@Bean
	public SecurityFilterChain userChain(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
        .logout(logout -> logout
        	.invalidateHttpSession(true)
            .deleteCookies("SESSION")
        )
		.exceptionHandling(exceptionHandling -> exceptionHandling
				.authenticationEntryPoint(new BasicAuthenticationEntryPoint())
		)
		.oauth2Login(oauth2Login -> oauth2Login
				.userInfoEndpoint()
				.userService(customOAuth2UserService)
		);
		return http.build();
	}

}
