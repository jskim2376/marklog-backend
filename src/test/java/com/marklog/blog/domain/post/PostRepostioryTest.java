package com.marklog.blog.domain.post;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PostRepostioryTest {

	@Autowired
	PostRepository postRepository;
	
	@AfterEach
	public void cleanup() {
		postRepository.deleteAll();
	}
	
	@Test
	public void load_post_save() {
		String title="테스트게시글";
		String content="테스트본문";
		
		postRepository.save(Post.builder().title(title).content(content).build());
		
		java.util.List<Post> postList = postRepository.findAll();
		
		Post post = postList.get(0);
		assertThat(post.getTitle()).isEqualTo(title);
		assertThat(post.getContent()).isEqualTo(content);
		System.out.println(post.getTitle());
	}
	
	@Test
	private void baseTimeEntityTest() {
		//given
		LocalDateTime now = LocalDateTime.of(2019, 6,4,0,0,0);
		postRepository.save(Post.builder()
				.title("title")
				.content("content")
				.build());
		
		//when
		List<Post> postList = postRepository.findAll();
		
		//then
		Post post = postList.get(0);
		
		System.out.println(">>>>>>> createDate="+post.getCreatedDate()+", modifiedDate="+post.getModifiedDate());
		
		assertThat(post.getCreatedDate()).isAfter(now);
		assertThat(post.getModifiedDate()).isAfter(now);
	}
}
