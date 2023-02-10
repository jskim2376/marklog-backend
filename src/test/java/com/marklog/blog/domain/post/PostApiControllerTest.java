package com.marklog.blog.domain.post;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.marklog.blog.web.dto.PostResponseDto;
import com.marklog.blog.web.dto.PostSaveRequestDto;
import com.marklog.blog.web.dto.PostUpdateRequestDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostApiControllerTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private PostRepository postRepository;

	@AfterEach
	public void tearDown() throws Exception{
		postRepository.deleteAll();
	}

	@Test
	public void Post_reigster() throws Exception{
		//given
		String title = "title";
		String content = "contetn";

		PostSaveRequestDto requestDto=PostSaveRequestDto
				.builder()
				.title(title)
				.content(content)
				.build();

		String url = "http://localhost:"+port+"/api/v1/post";

		//when
		ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);
		//then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isGreaterThan(0L);

		Post post = postRepository.findAll().get(0);

		assertThat(post.getTitle()).isEqualTo(title);
		assertThat(post.getContent()).isEqualTo(content);

	}
	@Test
	public void Post_get() throws Exception{
		//given
		Post savedPost = postRepository.save(Post.builder()
				.title("title")
				.content("content")
				.build());

		Long postId = savedPost.getId();
		String url_get= "http://localhost:"+port+"/api/v1/post/"+postId;
		System.out.println(url_get);

		//when
		PostResponseDto responseEntityGet  = restTemplate.getForObject(url_get, PostResponseDto.class);

		//then
		assertThat(responseEntityGet).isEqualTo(HttpStatus.OK);
		PostResponseDto resdto = responseEntityGet;
		System.out.println("-----------------------------------------------");
		System.out.println(resdto.getTitle());
	}
	@Test
	public void Post_modify() throws Exception{
		//given
		Post savedPost = postRepository.save(Post.builder()
				.title("title")
				.content("content")
				.build());

		Long updatedId = savedPost.getId();
		String expedctedTitle = "title2";
		String expectedContent = "content2";

		PostUpdateRequestDto requestDto = PostUpdateRequestDto.builder()
				.title(expedctedTitle)
				.content(expectedContent)
				.build();

		String url = "http://localhost:"+port+"/api/v1/post/"+updatedId;
		HttpEntity<PostUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

		//when
		ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT,requestEntity, Long.class);

		//then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isGreaterThan(0L);
		Post post = postRepository.findAll().get(0);
		assertThat(post.getTitle()).isEqualTo(expedctedTitle);
		assertThat(post.getContent()).isEqualTo(expectedContent);
	}
}
