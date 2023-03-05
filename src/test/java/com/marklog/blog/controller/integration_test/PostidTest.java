package com.marklog.blog.controller.integration_test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostidTest {
	@LocalServerPort
	private int port; 	
	WebClient wc;
	ObjectMapper objectMapper;
	
	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	UserRepository userRepository;
	User user1;
	String accessToken1;

	@Autowired
	PostRepository postRepository;
	Post post;
	Post post2;
	String uri;
	
	@BeforeAll
	public void setUp() {
		wc = WebClient.create("http://localhost:" + port);
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		String name = "name";
		String email = "postTbkj111sest@gmail.com";
		String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String title = "title";
		String introduce = "introduce";

		user1 = new User(name, email, picture, title, introduce, Role.USER);
		userRepository.save(user1);
		accessToken1 = jwtTokenProvider.createAccessToken(user1.getId(), email);

		String postThumbnail = "thumbnail";
		String postSummary = "summary";
		String postTitle = "post title";
		String postContent = "post content";
		post = new Post(postThumbnail, postSummary, postTitle, postContent, user1, null);
		postRepository.save(post);
		post2 = new Post(postThumbnail, postSummary, postTitle, postContent, user1, null);
		postRepository.save(post2);
	}
	
	public void createPostLike(Long id) {
		String uri = "/api/v1/post/"+id+"/like";
		// when
		wc.post().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.contentType(MediaType.APPLICATION_JSON).retrieve().toEntity(String.class).block();
	}

	
	@Test
	public void testPostLikeSave() {
		// given
		Long id = post.getId();
		String uri = "/api/v1/post/"+id+"/like";

		// when
		ResponseEntity<String> responseEntity = wc.post().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.contentType(MediaType.APPLICATION_JSON).retrieve().toEntity(String.class).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	public void testPostLikeSave_글이없을때() {
		// given
		String uri = "/api/v1/post/"+0L+"/like";

		// when
		ResponseEntity<String> responseEntity = wc.post().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.contentType(MediaType.APPLICATION_JSON).exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testPostLikeDelete() {
		// given
		Long id = post.getId();
		createPostLike(id);
		String uri = "/api/v1/post/"+id+"/like";

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void testPostLikeDelete_글이없을때() {
		// given
		String uri = "/api/v1/post/"+0L+"/like";

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testPostLikeDelete_좋아요가없을때() {
		// given
		Long id = post2.getId();
		String uri = "/api/v1/post/"+id+"/like";

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

}
