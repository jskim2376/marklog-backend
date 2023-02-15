package com.marklog.blog.domain.post;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

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
public class PostTest {
	@LocalServerPort
	private int port;
	@Autowired
	UserRepository userRepository;
	@Autowired
	JwtTokenProvider jwtTokenProvider;

	WebClient wc;
	ObjectMapper objectMapper;
	User user1;
	String accessToken1;
	String accessToken2;
	String accessTokenAdmin;

	String uri = "/api/v1/post/";
	String postTitle = "post title";
	String postContent = "post content";
	@BeforeAll
	public void setUp() {
		wc = WebClient.create("http://localhost:" + port);
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		String name = "name";
		String email = "postTest@gmail.com";
		String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String title = "title";
		String introduce = "introduce";

		user1 = new User(name, email, picture, title, introduce, Role.USER);
		userRepository.save(user1);
		accessToken1 = jwtTokenProvider.createAccessToken(user1.getId(), email);
		System.out.println(user1.getId());

		User user2 = new User(name, 222 + email, picture, title, introduce, Role.USER);
		userRepository.save(user2);
		accessToken2 = jwtTokenProvider.createAccessToken(user2.getId(), 2 + email);
		System.out.println(user2.getId());

		User user3 = new User(name, 333 + email, picture, title, introduce, Role.ADMIN);
		userRepository.save(user3);
		accessTokenAdmin = jwtTokenProvider.createAccessToken(user3.getId(), 3 + email);

	}

	public Long createPost() {
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(postTitle, postContent, user1.getId(), null);
		ResponseEntity<String> responseEntity = wc.post().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.body(Mono.just(postSaveRequestDto), PostSaveRequestDto.class).retrieve().toEntity(String.class)
				.block();

		HttpHeaders header = responseEntity.getHeaders();
		// then
		String location = header.getLocation().toString();
		return Long.valueOf(location.substring(uri.length()));
	}

	@Test
	public void testPostPost() throws JsonMappingException, JsonProcessingException {
		// given
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(postTitle, postContent, user1.getId(), null);

		// when
		ResponseEntity<String> responseEntity = wc.post().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.body(Mono.just(postSaveRequestDto), PostSaveRequestDto.class).retrieve().toEntity(String.class)
				.block();

		// then-ready
		HttpHeaders header = responseEntity.getHeaders();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(header.getLocation().toString()).startsWith(uri);
	}

	@Test
	public void testPostPost_태그랑() throws JsonMappingException, JsonProcessingException {
		// given
		List<String> tags = new ArrayList<>();
		tags.add("hihi");
		tags.add("tag2");

		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(postTitle, postContent, user1.getId(), tags);

		// when
		ResponseEntity<String> responseEntity = wc.post().uri(uri)
				.header("Authorization", "Bearer " + accessToken1)
				.body(Mono.just(postSaveRequestDto), PostSaveRequestDto.class).retrieve().toEntity(String.class)
				.block();

		// then-ready
		HttpHeaders header = responseEntity.getHeaders();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(header.getLocation().toString()).startsWith(uri);
		throw new JsonMappingException("asd");
	}

	@Test
	public void testPostPost_인증이_없을때() throws JsonMappingException, JsonProcessingException {
		// given
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(postTitle, postContent, null, null);
		// when
		ResponseEntity<String> responseEntity = wc.post().uri(uri)
				.body(Mono.just(postSaveRequestDto), PostSaveRequestDto.class)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void testGetPost() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createPost();

		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri + id).retrieve().toEntity(String.class)
				.block();

		// then-ready
		TestPostResponseDto testPostResponseDto = objectMapper.readValue(responseEntity.getBody(),
				TestPostResponseDto.class);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(testPostResponseDto.getTitle()).isEqualTo(postTitle);
		assertThat(testPostResponseDto.getContent()).isEqualTo(postContent);
	}

	@Test
	public void testGetPost_게시글이_없을때() throws JsonMappingException, JsonProcessingException {
		// given
		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri + 0L)
				.header("Authorization", "Bearer " + accessTokenAdmin)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testPutPost() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createPost();

		String newPostTitle = "new post title";
		String newPostContent = "new post content";
		PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(newPostTitle, newPostContent, null);

		// when
		ResponseEntity<String> putResponseEntity = wc.put().uri(uri + id)
				.header("Authorization", "Bearer " + accessToken1)
				.body(Mono.just(postUpdateRequestDto), PostUpdateRequestDto.class)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then-ready
		HttpHeaders header = putResponseEntity.getHeaders();
		
		ResponseEntity<String> getResponseEntity = wc.get().uri(header.getLocation().toString())
				.header("Authorization", "Bearer " + accessToken1).retrieve().toEntity(String.class).block();
		TestPostResponseDto getPostResponseDto = objectMapper.readValue(getResponseEntity.getBody(),
				TestPostResponseDto.class);

		// then
		assertThat(header.getLocation().toString()).isEqualTo(uri+id);
		assertThat(putResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(getPostResponseDto.getTitle()).isEqualTo(postUpdateRequestDto.getTitle());
		assertThat(getPostResponseDto.getContent()).isEqualTo(postUpdateRequestDto.getContent());
		assertThat(getPostResponseDto.getCreatedDate()).isBefore(getPostResponseDto.getModifiedDate());
	}

	@Test
	public void testPutPost_게시글이_없을때() throws JsonMappingException, JsonProcessingException {
		// given
		String newPostTitle = "new post title";
		String newPostContent = "new post content";
		PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(newPostTitle, newPostContent, null);

		// when
		ResponseEntity<String> responseEntity = wc.put().uri(uri + 0L)
				.header("Authorization", "Bearer " + accessTokenAdmin)
				.body(Mono.just(postUpdateRequestDto), PostUpdateRequestDto.class)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testPutPost_인증이_없을때() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createPost();

		String newPostTitle = "new post title";
		String newPostContent = "new post content";
		PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(newPostTitle, newPostContent, null);

		// when
		ResponseEntity<String> responseEntity = wc.put().uri(uri + id)
				.body(Mono.just(postUpdateRequestDto), PostUpdateRequestDto.class)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void testPutPost_본인소유가_아닐때() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createPost();

		String newPostTitle = "new post title";
		String newPostContent = "new post content";
		PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(newPostTitle, newPostContent, null);

		// when
		ResponseEntity<String> responseEntity = wc.put().uri(uri + id)
				.header("Authorization", "Bearer " + accessToken2)
				.body(Mono.just(postUpdateRequestDto), PostUpdateRequestDto.class)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void testPutPost_ADMIN일때() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createPost();

		String newPostTitle = "new post title";
		String newPostContent = "new post content";
		PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(newPostTitle, newPostContent, null);

		// when
		ResponseEntity<String> responseEntity = wc.put().uri(uri + id)
				.header("Authorization", "Bearer " + accessTokenAdmin)
				.body(Mono.just(postUpdateRequestDto), PostUpdateRequestDto.class)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		HttpHeaders header = responseEntity.getHeaders();
		// then
		assertThat(header.getLocation().toString()).isEqualTo(uri+id);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void testDeletePost() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createPost();

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri + id)
				.header("Authorization", "Bearer " + accessToken1)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		ResponseEntity<String> responseEntity2 = wc.get().uri(uri + id)
				.header("Authorization", "Bearer " + accessToken1)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testDeletePost_게시글이_없을때() throws JsonMappingException, JsonProcessingException {
		// given
		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri + 0L)
				.header("Authorization", "Bearer " + accessTokenAdmin)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testDeletePost_인증이_없을때() throws JsonMappingException, JsonProcessingException {
		Long id = createPost();

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri + id)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void testDeletePost_본인소유가_아닐때() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createPost();

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri + id)
				.header("Authorization", "Bearer " + accessToken2)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void testDeletePost_ADMIN일때() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createPost();

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri + id)
				.header("Authorization", "Bearer " + accessTokenAdmin)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

}