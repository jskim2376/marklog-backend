package com.marklog.blog.controller.integration_test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.json.JSONException;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.controller.dto.PostCommentRequestDto;
import com.marklog.blog.controller.dto.PostCommentResponseDto;
import com.marklog.blog.controller.dto.PostCommentUpdateRequestDto;
import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.post.comment.PostComment;
import com.marklog.blog.domain.post.comment.PostCommentRepository;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostCommentTest {
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

	@Autowired
	PostRepository postRepository;
	Post post;

	@Autowired
	PostCommentRepository postCommentRepository;
	String uri;
	String postCommentContent = "comment";

	@BeforeAll
	public void setUp() {
		wc = WebClient.create("http://localhost:" + port);
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		String name = "name";
		String email = "postTbkjsest@gmail.com";
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

		String postThumbnail = "thumbnail";
		String postSummary = "summary";
		String postTitle = "post title";
		String postContent = "post content";
		post = new Post(postThumbnail, postSummary, postTitle, postContent, user1, null);
		postRepository.save(post);

		uri = "/api/v1/post/" + post.getId() + "/comment/";
	}

	public PostComment createPostComment(String content) {
		return postCommentRepository.save(new PostComment(post, user1, content));
	}

	public PostComment createPostCommentChild(PostComment postComment, String content) {
		PostComment child = new PostComment(post, user1, content);
		child.setParent(postComment);
		return postCommentRepository.save(child);
	}

	@Test
	public void testPostPostComment() throws JsonProcessingException {
		// given
		PostCommentRequestDto postCommentRequestDto = new PostCommentRequestDto(null, postCommentContent);
		// when
		ResponseEntity<String> responseEntity = wc.post().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(objectMapper.writeValueAsString(postCommentRequestDto)).retrieve().toEntity(String.class)
				.block();

		// then-ready
		HttpHeaders header = responseEntity.getHeaders();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(header.getLocation().toString()).startsWith(uri);
	}

	@Test
	public void testPostPostComment_child() throws JsonProcessingException {
		// given
		PostComment postComment = createPostComment(postCommentContent);
		PostCommentRequestDto postCommentRequestDto = new PostCommentRequestDto(postComment.getId(),
				postCommentContent);

		// when
		ResponseEntity<String> responseEntity = wc.post().uri(uri).header("Authorization", "Bearer " + accessToken1)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(objectMapper.writeValueAsString(postCommentRequestDto)).retrieve().toEntity(String.class)
				.block();

		// then-ready
		HttpHeaders header = responseEntity.getHeaders();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(header.getLocation().toString()).startsWith(uri);
	}

	@Test
	public void testPostPostComment_인증이_없을때() throws JsonProcessingException {
		// given
		PostCommentRequestDto postCommentRequestDto = new PostCommentRequestDto(null, postCommentContent);

		// when
		ResponseEntity<String> responseEntity = wc.post().uri(uri).contentType(MediaType.APPLICATION_JSON)
				.bodyValue(objectMapper.writeValueAsString(postCommentRequestDto))
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

	}

	@Test
	public void testFindAllByPost() throws JsonMappingException, JsonProcessingException {
		// given
		String newContent1 = "findAllByPost_1";
		PostComment postComment1 = createPostComment(newContent1);
		PostCommentResponseDto postCommentResponseDto1 = PostCommentResponseDto.toDto(postComment1);
		String newContent2 = "findAllByPost_2";
		PostComment postComment2 = createPostComment(newContent2);
		PostCommentResponseDto postCommentResponseDto2 = PostCommentResponseDto.toDto(postComment2);

		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then-ready
		List<PostCommentResponseDto> postCommentResponseDtos = objectMapper.readValue(responseEntity.getBody(),
				new TypeReference<List<PostCommentResponseDto>>() {
				});
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(postCommentResponseDtos.contains(postCommentResponseDto1)).isTrue();
		assertThat(postCommentResponseDtos.contains(postCommentResponseDto2)).isTrue();
	}

	@Test
	public void testFindAllByPostWithChild() throws JSONException, JsonMappingException, JsonProcessingException {
		// given
		String newContent1 = "findAllByPost_1";
		PostComment postComment1 = createPostComment(newContent1);

		String newContentChild1 = "findAllByPost_child1";
		PostComment postCommentChild1 = createPostCommentChild(postComment1, newContentChild1);

		String newContentChild2 = "findAllByPost_child1";
		PostComment postCommentChild2 = createPostCommentChild(postCommentChild1, newContentChild2);

		PostCommentResponseDto postCommentResponseDto1 = PostCommentResponseDto.toDto(postComment1);
		PostCommentResponseDto postCommentChildResponseDto1 = PostCommentResponseDto.toDto(postCommentChild1);
		PostCommentResponseDto postCommentChildResponseDto2 = PostCommentResponseDto.toDto(postCommentChild2);

		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then-ready
		List<PostCommentResponseDto> postCommentResponseDtos = objectMapper.readValue(responseEntity.getBody(),
				new TypeReference<List<PostCommentResponseDto>>() {
				});
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(postCommentResponseDtos.contains(postCommentResponseDto1)).isTrue();
		PostCommentResponseDto responsePostCommentChildResponseDto1 = postCommentResponseDtos
				.get(postCommentResponseDtos.size() - 1).getChildList().get(0);
		assertThat(responsePostCommentChildResponseDto1).isEqualTo(postCommentChildResponseDto1);
		PostCommentResponseDto responsePostCommentChildResponseDto2 = responsePostCommentChildResponseDto1
				.getChildList().get(0);
		assertThat(responsePostCommentChildResponseDto2).isEqualTo(postCommentChildResponseDto2);

	}

	@Test
	public void testGetPostComment() throws JsonMappingException, JsonProcessingException {
		// given
		String newContent = "testGetPostComment";
		PostComment postComment = createPostComment(newContent);

		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri + postComment.getId()).retrieve()
				.toEntity(String.class).block();

		// then-ready
		PostCommentResponseDto getPostComment = objectMapper.readValue(responseEntity.getBody(),
				PostCommentResponseDto.class);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getPostComment.getContent()).isEqualTo(newContent);
	}

	@Test
	public void testGetPostCommentWithChild() throws JsonMappingException, JsonProcessingException {
		// given
		String newContent = "testGetPostComment";
		PostComment postComment = createPostComment(newContent);
		String newContentChild1 = "findAllByPost_child1";
		PostComment postCommentChild1 = createPostCommentChild(postComment, newContentChild1);
		String newContentChild2 = "findAllByPost_child1";
		PostComment postCommentChild2 = createPostCommentChild(postCommentChild1, newContentChild2);

		PostCommentResponseDto postCommentResponseDto1 = PostCommentResponseDto.toDto(postComment);
		PostCommentResponseDto postCommentChildResponseDto1 = PostCommentResponseDto.toDto(postCommentChild1);
		PostCommentResponseDto postCommentChildResponseDto2 = PostCommentResponseDto.toDto(postCommentChild2);

		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri + postComment.getId()).retrieve()
				.toEntity(String.class).block();

		// then-ready
		PostCommentResponseDto getPostComment = objectMapper.readValue(responseEntity.getBody(),
				PostCommentResponseDto.class);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getPostComment).isEqualTo(postCommentResponseDto1);
		assertThat(getPostComment.getChildList().get(0)).isEqualTo(postCommentChildResponseDto1);
		assertThat(getPostComment.getChildList().get(0).getChildList().get(0)).isEqualTo(postCommentChildResponseDto2);
	}

	@Test
	public void testGetPostComment_코멘트가_없을때() {
		ResponseEntity<String> responseEntity = wc.get().uri(uri + 0L)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

	}

	@Test
	public void testPutPostComment() throws JsonProcessingException {
		// given
		String newContent = "testPutComment";
		PostComment postComment = createPostComment(newContent);
		String updatedContent = "updatedComment";
		PostCommentUpdateRequestDto postCommentUpdateRequestDto = new PostCommentUpdateRequestDto(updatedContent);
		// when
		ResponseEntity<String> putResponseEntity = wc.put().uri(uri + postComment.getId())
				.header("Authorization", "Bearer " + accessToken1).contentType(MediaType.APPLICATION_JSON)
				.bodyValue(objectMapper.writeValueAsString(postCommentUpdateRequestDto))
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then-ready
		HttpHeaders header = putResponseEntity.getHeaders();

		// then
		assertThat(header.getLocation().toString()).isEqualTo(uri + postComment.getId());
		assertThat(putResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// when2
		ResponseEntity<String> getResponseEntity = wc.get().uri(header.getLocation().toString())
				.header("Authorization", "Bearer " + accessToken1).retrieve().toEntity(String.class).block();
		// then-ready
		PostCommentResponseDto getPostResponseDto = objectMapper.readValue(getResponseEntity.getBody(),
				PostCommentResponseDto.class);
		// then2
		assertThat(getPostResponseDto.getContent()).isEqualTo(updatedContent);
	}

	@Test
	public void testPutPostComment_게시글_없을때() throws JsonProcessingException {
		// given
		String newContent = "testPutComment";
		PostCommentUpdateRequestDto postCommentUpdateRequestDto = new PostCommentUpdateRequestDto(newContent);
		// when
		ResponseEntity<String> putResponseEntity = wc.put().uri(uri + 0L)
				.header("Authorization", "Bearer " + accessToken1).contentType(MediaType.APPLICATION_JSON)
				.bodyValue(objectMapper.writeValueAsString(postCommentUpdateRequestDto))
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(putResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void testPutPostComment_게시글_없을때_어드민() throws JsonProcessingException {
		// given
		String newContent = "testPutComment";
		PostCommentUpdateRequestDto postCommentUpdateRequestDto = new PostCommentUpdateRequestDto(newContent);
		// when
		ResponseEntity<String> putResponseEntity = wc.put().uri(uri + 0L)
				.header("Authorization", "Bearer " + accessTokenAdmin).contentType(MediaType.APPLICATION_JSON)
				.bodyValue(objectMapper.writeValueAsString(postCommentUpdateRequestDto))
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(putResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testPutPostComment_인증이_없을때() throws JsonProcessingException {
		String newContent = "testPutComment";
		PostComment postComment = createPostComment(newContent);
		String updatedContent = "updatedComment";
		PostCommentUpdateRequestDto postCommentUpdateRequestDto = new PostCommentUpdateRequestDto(updatedContent);

		ResponseEntity<String> putResponseEntity = wc.put().uri(uri + postComment.getId())
				.header("Authorization", "Bearer " + accessToken1).contentType(MediaType.APPLICATION_JSON)
				.bodyValue(objectMapper.writeValueAsString(postCommentUpdateRequestDto))
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		assertThat(putResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void testPutPostComment_본인소유가_아닐때() throws JsonProcessingException {
		String newContent = "testPutComment";
		PostComment postComment = createPostComment(newContent);
		String updatedContent = "updatedComment";
		PostCommentUpdateRequestDto postCommentUpdateRequestDto = new PostCommentUpdateRequestDto(updatedContent);

		ResponseEntity<String> putResponseEntity = wc.put().uri(uri + postComment.getId())
				.header("Authorization", "Bearer " + accessToken2).contentType(MediaType.APPLICATION_JSON)
				.bodyValue(objectMapper.writeValueAsString(postCommentUpdateRequestDto))
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(putResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void testPutPostComment_어드민일떄() throws JsonProcessingException {
		// given
		String newContent = "testPutComment";
		PostComment postComment = createPostComment(newContent);
		String updatedContent = "updatedComment";
		PostCommentUpdateRequestDto postCommentUpdateRequestDto = new PostCommentUpdateRequestDto(updatedContent);

		// when
		ResponseEntity<String> putResponseEntity = wc.put().uri(uri + postComment.getId())
				.header("Authorization", "Bearer " + accessTokenAdmin).contentType(MediaType.APPLICATION_JSON)
				.bodyValue(objectMapper.writeValueAsString(postCommentUpdateRequestDto))
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then-ready
		HttpHeaders header = putResponseEntity.getHeaders();

		// then
		assertThat(header.getLocation().toString()).isEqualTo(uri + postComment.getId());
		assertThat(putResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponseEntity = wc.get().uri(header.getLocation().toString())
				.header("Authorization", "Bearer " + accessToken1).retrieve().toEntity(String.class).block();
		PostCommentResponseDto getPostResponseDto = objectMapper.readValue(getResponseEntity.getBody(),
				PostCommentResponseDto.class);

		assertThat(getPostResponseDto.getContent()).isEqualTo(updatedContent);
	}

	@Test
	public void testDeletePostComment() {
		// given
		String newContent = "testDeletePostComment";
		PostComment postComment = createPostComment(newContent);

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri + postComment.getId())
				.header("Authorization", "Bearer " + accessToken1)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// when2
		ResponseEntity<String> responseEntity2 = wc.get().uri(uri + postComment.getId())
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testDeletePostComment_게시글이_없을때() {
		// given
		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri + 0L)
				.header("Authorization", "Bearer " + accessToken1)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

	}

	@Test
	public void testDeletePostComment_게시글이_없을때_어드민() {
		// given
		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri + 0L)
				.header("Authorization", "Bearer " + accessTokenAdmin)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

	}

	@Test
	public void testDeletePostComment_인증이_없을때() {
		// given
		String newContent = "testDeletePostComment";
		PostComment postComment = createPostComment(newContent);

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri + postComment.getId())
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void testDeletePostComment_본인소유가_아닐떄() {
		// given
		String newContent = "testDeletePostComment";
		PostComment postComment = createPostComment(newContent);

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri + postComment.getId())
				.header("Authorization", "Bearer " + accessToken2)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

	}

	@Test
	public void testDeletePostComment_어드민일때() {
		// given
		String newContent = "testDeletePostComment";
		PostComment postComment = createPostComment(newContent);

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri + postComment.getId())
				.header("Authorization", "Bearer " + accessTokenAdmin)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

	}

}
