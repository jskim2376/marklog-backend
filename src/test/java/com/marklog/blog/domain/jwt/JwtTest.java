package com.marklog.blog.domain.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;
import com.marklog.blog.dto.TestPostIdResponseDto;
import com.marklog.blog.dto.TestPostResponseDto;
import com.marklog.blog.web.dto.PostSaveRequestDto;
import com.marklog.blog.web.dto.PostUpdateRequestDto;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JwtTest {
	@LocalServerPort
	private int port;
	@Autowired
	UserRepository userRepository;
	@Autowired
	JwtTokenProvider jwtTokenProvider;

	WebClient wc;
	ObjectMapper objectMapper;
	String email = "postTest@gmail.com";
	User user1;
	
	@BeforeAll
	public void setUp() {
		String name = "name";
		String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String title = "title";
		String introduce = "introduce";
		wc = WebClient.create("http://localhost:" + port);
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		user1 = new User(name, email, picture, title, introduce, Role.USER);
		userRepository.save(user1);
		
	}

	@Test
	public void testJwtCheck() throws JsonMappingException, JsonProcessingException {
		// given
		String uri ="/api/v1/token/check";
		String accessToken1 = jwtTokenProvider.createAccessToken(user1.getId(), email);
		
		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	
	@Test
	public void testJwtRefresh() throws JsonMappingException, JsonProcessingException {
		// given
		String uri ="/api/v1/token/refresh";
		String refreshToken = jwtTokenProvider.createRefreshToken(user1.getId(), email);
		
		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri).cookie("refresh_token", refreshToken)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}
