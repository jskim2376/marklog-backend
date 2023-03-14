package com.marklog.blog.controller.integration_test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.domain.notice.Notice;
import com.marklog.blog.domain.notice.NoticeRepository;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;
import com.marklog.blog.dto.NoticeResponseDto;
import com.marklog.blog.dto.PostCommentSaveRequestDto;
import com.marklog.blog.dto.PostSaveRequestDto;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NoticeTest {
	@LocalServerPort
	private int port;
	WebClient wc;
	ObjectMapper objectMapper;
	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	UserRepository userRepository;

	@Autowired
	NoticeRepository noticeRepository;

	User user1;
	String accessToken1;
	String accessToken2;
	String accessTokenAdmin;

	String uri = "/api/v1/notice/";
	Long noticeNotExist = 0L;
	String noticeContent = "noticeContent";

	@BeforeAll
	public void setUp() {
		wc = WebClient.create("http://localhost:" + port);
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		String name = "name";
		String email = "noticeSetup@gmail.com";
		String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String title = "title";
		String introduce = "introduce";

		user1 = new User(name, email, picture, title, introduce, Role.USER);
		userRepository.save(user1);
		accessToken1 = jwtTokenProvider.createAccessToken(user1.getId(), email);

		User user2 = new User(name, 2 + email, picture, title, introduce, Role.USER);
		userRepository.save(user2);
		accessToken2 = jwtTokenProvider.createAccessToken(user2.getId(), 2 + email);

		User user3 = new User(name, 3 + email, picture, title, introduce, Role.ADMIN);
		userRepository.save(user3);
		accessTokenAdmin = jwtTokenProvider.createAccessToken(user3.getId(), 3 + email);
	}

	@AfterEach
	public void tearDown() {
		noticeRepository.deleteAll();
	}

	public Notice createNotice() {
		Notice notice = new Notice(noticeContent, user1);
		return noticeRepository.save(notice);
	}

	public Long createPost() {
		String uri = "/api/v1/post/";
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto("title", "content", null);
		ResponseEntity<String> responseEntity = wc.post().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.body(Mono.just(postSaveRequestDto), PostSaveRequestDto.class).retrieve().toEntity(String.class)
				.block();

		HttpHeaders header = responseEntity.getHeaders();
		// then
		String location = header.getLocation().toString();
		return Long.valueOf(location.substring(uri.length()));
	}

	public Long createPostComment(Long postId) {
		String uri = "/api/v1/post/" + postId + "/comment/";

		PostCommentSaveRequestDto postSaveRequestDto = new PostCommentSaveRequestDto(null, "comment");
		ResponseEntity<String> responseEntity = wc.post().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.body(Mono.just(postSaveRequestDto), PostSaveRequestDto.class).retrieve().toEntity(String.class)
				.block();

		HttpHeaders header = responseEntity.getHeaders();
		// then
		String location = header.getLocation().toString();
		return Long.valueOf(location.substring(uri.length()));
	}

	@Test
	public void testGetAllUnCheckNotice() throws IOException {
		// given
		String uri = this.uri + user1.getId() + "/uncheck";
		createNotice();
		createNotice();
		createNotice();

		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.retrieve().toEntity(String.class).block();

		// then-ready
		ObjectReader reader = objectMapper.readerFor(new TypeReference<List<NoticeResponseDto>>() {
		});
		List<NoticeResponseDto> noticeResponseDtos = reader.readValue(responseEntity.getBody());

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(noticeResponseDtos.size()).isEqualTo(3);
		assertThat(noticeResponseDtos.get(0).getCheckFlag()).isFalse();
	}

	@Test
	public void testGetAllUnCheckNotice_result_zero() throws IOException {
		// given
		String uri = this.uri + user1.getId() + "/uncheck";

		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.retrieve().toEntity(String.class).block();

		// then-ready
		ObjectReader reader = objectMapper.readerFor(new TypeReference<List<NoticeResponseDto>>() {
		});
		List<NoticeResponseDto> noticeResponseDtos = reader.readValue(responseEntity.getBody());

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(noticeResponseDtos.size()).isEqualTo(0);
	}

	@Test
	public void testGetAllUnCheckNotice_no_auth() throws IOException {
		// given
		String uri = this.uri + user1.getId() + "/uncheck";
		createNotice();
		createNotice();
		createNotice();

		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void testGetAllUnCheckNotice_Admin() throws IOException {
		// given
		String uri = this.uri + user1.getId() + "/uncheck";
		createNotice();
		createNotice();
		createNotice();

		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri).header("Authorization", "Bearer " + accessTokenAdmin)
				.retrieve().toEntity(String.class).block();

		// then-ready
		ObjectReader reader = objectMapper.readerFor(new TypeReference<List<NoticeResponseDto>>() {
		});
		List<NoticeResponseDto> noticeResponseDtos = reader.readValue(responseEntity.getBody());

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(noticeResponseDtos.size()).isEqualTo(3);
		assertThat(noticeResponseDtos.get(0).getCheckFlag()).isFalse();
	}

	@Test
	public void testGetAllUnCheckNotice_diffrent_auth() throws IOException {
		// given
		String uri = this.uri + user1.getId() + "/uncheck";
		createNotice();
		createNotice();
		createNotice();

		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri).header("Authorization", "Bearer " + accessToken2)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void testPostPostComment() throws JsonProcessingException {
		// given
		String uri = this.uri + user1.getId() + "/uncheck";
		Long postId = createPost();
		createPostComment(postId);

		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then-ready
		ObjectReader reader = objectMapper.readerFor(new TypeReference<List<NoticeResponseDto>>() {
		});
		List<NoticeResponseDto> noticeResponseDtos = reader.readValue(responseEntity.getBody());

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(noticeResponseDtos.size()).isEqualTo(1);
		assertThat(noticeResponseDtos.get(0).getContent().contains("댓글이")).isTrue();

	}

	@Test
	public void testCheckNotice() throws IOException {
		// given
		Notice notice = createNotice();
		String uri = this.uri + notice.getId();

		// when
		ResponseEntity<String> responseEntity = wc.put().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.retrieve().toEntity(String.class).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void testCheckNotice_not_found() throws IOException {
		// given
		String uri = this.uri + noticeNotExist;

		// when
		ResponseEntity<String> responseEntity = wc.put().uri(uri).header("Authorization", "Bearer " + accessTokenAdmin)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testCheckNotice_no_auth() throws IOException {
		// given
		Notice notice = createNotice();
		String uri = this.uri + notice.getId();

		// when
		ResponseEntity<String> responseEntity = wc.put().uri(uri)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void testCheckNotice_admin() throws IOException {
		// given
		Notice notice = createNotice();
		String uri = this.uri + notice.getId();

		// when
		ResponseEntity<String> responseEntity = wc.put().uri(uri).header("Authorization", "Bearer " + accessTokenAdmin)
				.retrieve().toEntity(String.class).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void testCheckNotice_diffrent_auth() throws IOException {
		// given
		Notice notice = createNotice();
		String uri = this.uri + notice.getId();

		// when
		ResponseEntity<String> responseEntity = wc.put().uri(uri).header("Authorization", "Bearer " + accessToken2)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void testDeleteNotice() throws IOException {
		// given
		Notice notice = createNotice();
		String uri = this.uri + notice.getId();

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.retrieve().toEntity(String.class).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void testDeleteNotice_not_found() throws IOException {
		// given
		String uri = this.uri + noticeNotExist;

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri)
				.header("Authorization", "Bearer " + accessTokenAdmin)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testDeleteNotice_no_auth() throws IOException {
		// given
		Notice notice = createNotice();
		String uri = this.uri + notice.getId();

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void testDeleteNotice_Admin() throws IOException {
		// given
		Notice notice = createNotice();
		String uri = this.uri + notice.getId();

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri)
				.header("Authorization", "Bearer " + accessTokenAdmin).retrieve().toEntity(String.class).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void testDeleteNotice_diffrent_auth() throws IOException {
		// given
		Notice notice = createNotice();
		String uri = this.uri + notice.getId();

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri).header("Authorization", "Bearer " + accessToken2)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}
}
