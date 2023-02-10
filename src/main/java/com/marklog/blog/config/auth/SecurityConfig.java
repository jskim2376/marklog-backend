package com.marklog.blog.config.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;

import com.marklog.blog.domain.user.Role;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.agent.builder.AgentBuilder.FallbackStrategy.Simple;
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
