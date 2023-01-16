package com.marklog.blog.config.auth;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.marklog.blog.domain.user.Role;

import lombok.RequiredArgsConstructor;
@SuppressWarnings("deprecation")
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	private final CustomOAuth2UserService customOAuth2UserService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http
			.csrf().disable()
			.headers().frameOptions().disable()
			.and()
				.authorizeRequests()
				.antMatchers("/", "/css/**", "/assets/**", "/images/**","/js/**","h2-console/**").permitAll()
				.antMatchers("/api/v1/**").hasRole(Role.USER.name())
				.anyRequest().authenticated()
//				.anyRequest().permitAll()
				
			.and()
				.logout()
				.logoutSuccessUrl("/")
			.and()
				.oauth2Login()
					.userInfoEndpoint()
						.userService(customOAuth2UserService);
	}
	
}
