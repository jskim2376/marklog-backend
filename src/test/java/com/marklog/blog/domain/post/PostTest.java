package com.marklog.blog.domain.post;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.Users;
import com.marklog.blog.domain.user.UsersRepository;
import com.marklog.blog.dto.TestPostIdResponseDto;
import com.marklog.blog.dto.TestPostResponseDto;
import com.marklog.blog.web.dto.PostSaveRequestDto;
import com.marklog.blog.web.dto.PostUpdateRequestDto;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class PostTest {
	@LocalServerPort
	private int port;

	@Autowired
	UsersRepository usersRepository;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	WebClient wc;
	ObjectMapper objectMapper;
	Users user1;
	Users user2;
	Users user3;
	String accessToken1;
	String accessToken2;
	String accessTokenAdmin;

	String postTitle = "post title";
	String postContent = "post content";

	@BeforeAll
	public void setUp() {
		wc = WebClient.create("http://localhost:" + port);
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		String name = "name";
		String email = "test@gmail.com";
		String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String title = "title";
		String introduce = "introduce";

		user1 = new Users(name, email, picture, title, introduce, Role.USER);
		usersRepository.save(user1);
		accessToken1 = jwtTokenProvider.createAccessToken(user1.getId(), email);

		user2 = new Users(name, 2 + email, picture, title, introduce, Role.USER);
		usersRepository.save(user2);
		accessToken2 = jwtTokenProvider.createAccessToken(user2.getId(), email);

		user3 = new Users(name, 3 + email, picture, title, introduce, Role.ADMIN);
		usersRepository.save(user3);
		accessTokenAdmin = jwtTokenProvider.createAccessToken(user3.getId(), email);

	}

	public Long createPost() {
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(postTitle, postContent, user1.getId(), null);
		ResponseEntity<String> responseEntity = wc.post().uri("/api/v1/post")
				.header("Authorization", "Bearer " + accessToken1)
				.body(Mono.just(postSaveRequestDto), PostSaveRequestDto.class).retrieve().toEntity(String.class)
				.block();

		// then-ready
		ObjectMapper objectMapper = new ObjectMapper();
		TestPostIdResponseDto testPostIdResponseDto;
		try {
			testPostIdResponseDto = objectMapper.readValue(responseEntity.getBody(), TestPostIdResponseDto.class);
			return testPostIdResponseDto.getId();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return 0L;
		}
	}

	@Test
	public void testPostPost() throws JsonMappingException, JsonProcessingException {
		// given
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(postTitle, postContent, user1.getId(), null);

		// when
		ResponseEntity<String> responseEntity = wc.post().uri("/api/v1/post")
				.header("Authorization", "Bearer " + accessToken1)
				.body(Mono.just(postSaveRequestDto), PostSaveRequestDto.class).retrieve().toEntity(String.class)
				.block();

		// then-ready
		TestPostIdResponseDto postIdResponseDto = objectMapper.readValue(responseEntity.getBody(),
				TestPostIdResponseDto.class);

		// then
		assertThat(postIdResponseDto.getId()).isGreaterThan(0);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	public void testPostPost_인증이_없을때() throws JsonMappingException, JsonProcessingException {
		// given
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(postTitle, postContent, null, null);
		// when
		ResponseEntity<String> responseEntity = wc.post().uri("/api/v1/post/")
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
		ResponseEntity<String> responseEntity = wc.get().uri("/api/v1/post/" + id).retrieve().toEntity(String.class)
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
		ResponseEntity<String> responseEntity = wc.get().uri("/api/v1/post/" + 0L)
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
		ResponseEntity<String> putResponseEntity = wc.put().uri("/api/v1/post/" + id)
				.header("Authorization", "Bearer " + accessToken1)
				.body(Mono.just(postUpdateRequestDto), PostUpdateRequestDto.class)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then-ready
		ResponseEntity<String> getResponseEntity = wc.get().uri("/api/v1/post/" + id)
				.header("Authorization", "Bearer " + accessToken1).retrieve().toEntity(String.class).block();
		TestPostIdResponseDto putPostIdResponseDto = objectMapper.readValue(putResponseEntity.getBody(),
				TestPostIdResponseDto.class);
		TestPostResponseDto getPostResponseDto = objectMapper.readValue(getResponseEntity.getBody(),
				TestPostResponseDto.class);

		// then
		assertThat(putResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(putPostIdResponseDto.getId()).isEqualTo(id);
		assertThat(getPostResponseDto.getTitle()).isEqualTo(postUpdateRequestDto.getTitle());
		assertThat(getPostResponseDto.getContent()).isEqualTo(postUpdateRequestDto.getContent());
	}

	@Test
	public void testPutPost_게시글이_없을때() throws JsonMappingException, JsonProcessingException {
		// given
		String newPostTitle = "new post title";
		String newPostContent = "new post content";
		PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(newPostTitle, newPostContent, null);

		// when
		ResponseEntity<String> responseEntity = wc.put().uri("/api/v1/post/" + 0L)
				.header("Authorization", "Bearer " + accessToken1)
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
		ResponseEntity<String> responseEntity = wc.put().uri("/api/v1/post/" + id)
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
		ResponseEntity<String> responseEntity = wc.put().uri("/api/v1/post/" + id)
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
		ResponseEntity<String> responseEntity = wc.put().uri("/api/v1/post/" + id)
				.header("Authorization", "Bearer " + accessTokenAdmin)
				.body(Mono.just(postUpdateRequestDto), PostUpdateRequestDto.class)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void testDeletePost() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createPost();

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri("/api/v1/post/" + id)
				.header("Authorization", "Bearer " + accessToken1)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		ResponseEntity<String> responseEntity2 = wc.get().uri("/api/v1/post/" + id)
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
		ResponseEntity<String> responseEntity = wc.delete().uri("/api/v1/post/" + 0L)
				.header("Authorization", "Bearer " + accessToken1)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testDeletePost_인증이_없을때() throws JsonMappingException, JsonProcessingException {
		Long id = createPost();

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri("/api/v1/post/" + id)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void testDeletePost_본인소유가_아닐때() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createPost();

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri("/api/v1/post/" + id)
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
		ResponseEntity<String> responseEntity = wc.delete().uri("/api/v1/post/" + id)
				.header("Authorization", "Bearer " + accessTokenAdmin)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

}
