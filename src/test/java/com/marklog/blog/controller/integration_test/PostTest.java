package com.marklog.blog.controller.integration_test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;
import com.marklog.blog.dto.PostListResponseDto;
import com.marklog.blog.dto.PostResponseDto;
import com.marklog.blog.dto.PostSaveRequestDto;
import com.marklog.blog.dto.PostUpdateRequestDto;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostTest {
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
	String accessToken2;
	String accessTokenAdmin;

	String uri = "/api/v1/post/";
	String postTitle = "post title";
	String postContent = "post content";
	String tagName = "tagName";
	
	@BeforeAll
	public void setUp() {
		wc = WebClient.create("http://localhost:" + port);
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		String name = "name";
		String email = "postTestemail@gmail.com";
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

	public Long createPost() {
		List<String> tags = new ArrayList<>();
		tags.add(tagName);
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(postTitle, postContent, tags);
		ResponseEntity<String> responseEntity = wc.post().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.body(Mono.just(postSaveRequestDto), PostSaveRequestDto.class).retrieve().toEntity(String.class)
				.block();

		HttpHeaders header = responseEntity.getHeaders();
		// then
		String location = header.getLocation().toString();
		return Long.valueOf(location.substring(uri.length()));
	}

	public Long createPost(String title, String content) {
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(title, content, null);
		ResponseEntity<String> responseEntity = wc.post().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.body(Mono.just(postSaveRequestDto), PostSaveRequestDto.class).retrieve().toEntity(String.class)
				.block();

		HttpHeaders header = responseEntity.getHeaders();
		// then
		String location = header.getLocation().toString();
		return Long.valueOf(location.substring(uri.length()));
	}

	public void createPostLike(Long id) {
		String uri = "/api/v1/post/" + id + "/like";
		// when
		wc.post().uri(uri).header("Authorization", "Bearer " + accessToken1).contentType(MediaType.APPLICATION_JSON)
				.retrieve().toEntity(String.class).block();
	}
	
	@Test
	public void testRecentPost() throws JSONException, IOException {
		// given
		String uri = this.uri + "/recent";
		String recentTitle = "recentPost";
		String recentContent = "search";
		Long postId = createPost(recentTitle, recentContent);
		createPostLike(postId);
		String sort = "id,desc";

		// when
		ResponseEntity<String> responseEntity = wc.get()
				.uri(uriBuilder -> uriBuilder.path(uri).queryParam("sort", sort).build()).retrieve()
				.toEntity(String.class).block();
		// then-ready
		ObjectReader reader = objectMapper.readerFor(new TypeReference<List<PostListResponseDto>>() {});

		JsonNode node = objectMapper.readTree(responseEntity.getBody());
		List<PostListResponseDto> postListResponseDtos = reader.readValue(node.get("content"));

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(postListResponseDtos.get(0).getTitle()).isEqualTo(recentTitle);
	}

	@Test
	public void testSearchPost() throws JSONException, IOException {
		// given
		String uri = this.uri+"/search";
		String searchTitle = "search title";
		String searchContent = "hhhhh";
		String text = "search";
		createPost(searchTitle, searchContent);
		// when
		ResponseEntity<String> responseEntity = wc.get()
				.uri(uriBuilder -> uriBuilder.path(uri).queryParam("text", text).build()).retrieve()
				.toEntity(String.class).block();

		// then-ready
		ObjectReader reader = objectMapper.readerFor(new TypeReference<List<PostListResponseDto>>() {
		});
		JsonNode node = objectMapper.readTree(responseEntity.getBody());
		List<PostListResponseDto> postListResponseDtos = reader.readValue(node.get("content"));

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(postListResponseDtos.get(0).getTitle().contains(text)).isTrue();
	}

	@Test
	public void testSearchPost_empty() throws JsonMappingException, JsonProcessingException, JSONException {
		// given
		String uri = this.uri+"/search";
		String text = "adqsadqwfwq";
		// when
		ResponseEntity<String> responseEntity = wc.get()
				.uri(uriBuilder -> uriBuilder.path(uri).queryParam("text", text).build()).retrieve()
				.toEntity(String.class).block();

		// then-ready
		JSONObject jsonObject = new JSONObject(responseEntity.getBody());
		Boolean empty = jsonObject.getBoolean("empty");

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(empty).isTrue();
	}
	
	@Test
	public void testTagName() throws JSONException, IOException {
		// given
		String uri = this.uri+"/tag";
		createPost();
		// when
		ResponseEntity<String> responseEntity = wc.get()
				.uri(uriBuilder -> uriBuilder.path(uri).queryParam("tag-name", tagName).build()).retrieve()
				.toEntity(String.class).block();

		// then-ready
		ObjectReader reader = objectMapper.readerFor(new TypeReference<List<PostListResponseDto>>() {});
		List<PostListResponseDto> postListResponseDtos = reader.readValue(responseEntity.getBody());

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(postListResponseDtos.get(0).getTitle().equals(postTitle)).isTrue();
		assertThat(postListResponseDtos.get(0).getTagList().get(0).getName().equals(tagName)).isTrue();
	}
	
	@Test
	public void testTagNameAndUserId() throws JSONException, IOException {
		// given
		String uri = this.uri+"/tag";
		createPost();
		// when
		ResponseEntity<String> responseEntity = wc.get()
				.uri(uriBuilder -> uriBuilder.path(uri).queryParam("tag-name", tagName).queryParam("user-id", user1.getId()).build()).retrieve()
				.toEntity(String.class).block();

		// then-ready
		ObjectReader reader = objectMapper.readerFor(new TypeReference<List<PostListResponseDto>>() {});
		List<PostListResponseDto> postListResponseDtos = reader.readValue(responseEntity.getBody());

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(postListResponseDtos.get(0).getTitle().equals(postTitle)).isTrue();
		assertThat(postListResponseDtos.get(0).getTagList().get(0).getName().equals(tagName)).isTrue();
	}


	@Test
	public void testPostPost() throws JsonMappingException, JsonProcessingException {
		// given
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(postTitle, postContent, null);

		// when
		ResponseEntity<String> responseEntity = wc.post().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.contentType(MediaType.APPLICATION_JSON).bodyValue(objectMapper.writeValueAsString(postSaveRequestDto))
				.retrieve().toEntity(String.class).block();

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

		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(postTitle, postContent, tags);

		// when
		ResponseEntity<String> responseEntity = wc.post().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.contentType(MediaType.APPLICATION_JSON).bodyValue(objectMapper.writeValueAsString(postSaveRequestDto))
				.retrieve().toEntity(String.class).block();

		// then-ready
		HttpHeaders header = responseEntity.getHeaders();

		ResponseEntity<String> responseEntity2 = wc.get().uri(header.getLocation().toString()).retrieve()
				.toEntity(String.class).block();
		PostResponseDto testPostResponseDto = objectMapper.readValue(responseEntity2.getBody(), PostResponseDto.class);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(header.getLocation().toString()).startsWith(uri);

		assertThat(testPostResponseDto.getTitle()).isEqualTo(postTitle);
		assertThat(testPostResponseDto.getContent()).isEqualTo(postContent);
		assertThat(testPostResponseDto.getTagList().get(0).getName()).isEqualTo(tags.get(0));

	}

	@Test
	public void testPostPost_인증이_없을때() throws JsonMappingException, JsonProcessingException {
		// given
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(postTitle, postContent, null);
		// when
		ResponseEntity<String> responseEntity = wc.post().uri(uri).contentType(MediaType.APPLICATION_JSON)
				.bodyValue(objectMapper.writeValueAsString(postSaveRequestDto))
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void testGetPost() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createPost();

		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri + id).retrieve().toEntity(String.class).block();

		// then-ready
		PostResponseDto testPostResponseDto = objectMapper.readValue(responseEntity.getBody(), PostResponseDto.class);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(testPostResponseDto.getTitle()).isEqualTo(postTitle);
		assertThat(testPostResponseDto.getContent()).isEqualTo(postContent);
		assertThat(testPostResponseDto.getLike()).isEqualTo(false);
	}

	@Test
	public void testGetPost_like() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createPost();
		createPostLike(id);

		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri + id).header("Authorization", "Bearer " + accessToken1)
				.retrieve().toEntity(String.class).block();

		// then-ready
		PostResponseDto testPostResponseDto = objectMapper.readValue(responseEntity.getBody(), PostResponseDto.class);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(testPostResponseDto.getTitle()).isEqualTo(postTitle);
		assertThat(testPostResponseDto.getContent()).isEqualTo(postContent);
		assertThat(testPostResponseDto.getLike()).isEqualTo(true);

	}

	@Test
	public void testGetPost_게시글이_없을때() throws JsonMappingException, JsonProcessingException {
		// given
		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri + 0L).header("Authorization", "Bearer " + accessToken1)
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
				.header("Authorization", "Bearer " + accessToken1).contentType(MediaType.APPLICATION_JSON)
				.bodyValue(objectMapper.writeValueAsString(postUpdateRequestDto))
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then-ready
		HttpHeaders header = putResponseEntity.getHeaders();

		// then
		assertThat(header.getLocation().toString()).isEqualTo(uri + id);
		assertThat(putResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponseEntity = wc.get().uri(header.getLocation().toString())
				.header("Authorization", "Bearer " + accessToken1).retrieve().toEntity(String.class).block();
		PostResponseDto getPostResponseDto = objectMapper.readValue(getResponseEntity.getBody(), PostResponseDto.class);
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
		ResponseEntity<String> responseEntity = wc.put().uri(uri + 0L).header("Authorization", "Bearer " + accessToken1)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(objectMapper.writeValueAsString(postUpdateRequestDto))
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void testPutPost_인증이_없을때() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createPost();

		String newPostTitle = "new post title";
		String newPostContent = "new post content";
		PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(newPostTitle, newPostContent, null);

		// when
		ResponseEntity<String> responseEntity = wc.put().uri(uri + id).contentType(MediaType.APPLICATION_JSON)
				.bodyValue(objectMapper.writeValueAsString(postUpdateRequestDto))
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
		ResponseEntity<String> responseEntity = wc.put().uri(uri + id).header("Authorization", "Bearer " + accessToken2)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(objectMapper.writeValueAsString(postUpdateRequestDto))
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
				.header("Authorization", "Bearer " + accessTokenAdmin).contentType(MediaType.APPLICATION_JSON)
				.bodyValue(objectMapper.writeValueAsString(postUpdateRequestDto))
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		HttpHeaders header = responseEntity.getHeaders();
		// then
		assertThat(header.getLocation().toString()).isEqualTo(uri + id);
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
