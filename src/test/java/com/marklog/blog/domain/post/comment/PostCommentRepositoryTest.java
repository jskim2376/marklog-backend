package com.marklog.blog.domain.post.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.tag.Tag;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;

@DataJpaTest
public class PostCommentRepositoryTest {
	@Autowired
	UserRepository userRepository;

	@Autowired
	PostRepository postRepository;

	@Autowired
	PostCommentRepository postCommentRepository;

	String title="title";
	String content="title";

	String name = "name";
	String email = "test@gmail.com";
	String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
	String userTitle = "myblog";
	String introduce = "introduce";

	@Test
	public void testPostCommentSave() {
		//given
		User user = new User(name,email, picture, userTitle, introduce, Role.USER);
		userRepository.save(user);
		Post post = new Post(title, content, user, null);
		postRepository.save(post);

		PostComment postComment = new PostComment(post, user, content);
		
		//when
		PostComment savedPostComment = postCommentRepository.save(postComment);
		
		//then
		assertThat(savedPostComment).isSameAs(postComment);
	}
	@Test
	public void testPostCommentFindAllByPost() {
		//given
		User user = new User(name,email, picture, userTitle, introduce, Role.USER);
		userRepository.save(user);
		Post post = new Post(title, content, user, null);
		postRepository.save(post);
		PostComment postComment = new PostComment(post,user, content);
		PostComment savedPostComment = postCommentRepository.save(postComment);
		
		//when
		List<PostComment> returnCommentList = postCommentRepository.findAllByPost(post);
		
		//then
		assertThat(returnCommentList.get(0)).isSameAs(savedPostComment);
	}
	@Test
	public void testPostCommentFindById() {
		//given
		User user = new User(name,email, picture, userTitle, introduce, Role.USER);
		userRepository.save(user);
		Post post = new Post(title, content, user, null);
		postRepository.save(post);
		PostComment postComment = new PostComment(post,user, content);
		PostComment savedPostComment = postCommentRepository.save(postComment);
		
		//when
		PostComment returnComment = postCommentRepository.findById(savedPostComment.getId()).get();
		
		//then
		assertThat(savedPostComment).isSameAs(returnComment);
	}

	@Test
	public void testPostCommentUpdate() {
		//given
		User user = new User(name,email, picture, userTitle, introduce, Role.USER);
		userRepository.save(user);
		Post post = new Post(title, content, user, null);
		postRepository.save(post);
		PostComment postComment = new PostComment(post,user, content);
		PostComment savedPostComment = postCommentRepository.save(postComment);
		PostComment returnComment = postCommentRepository.findById(savedPostComment.getId()).get();
		String newContent = "new content";
		
		//when
		returnComment.update(newContent);
		
		//then
		PostComment updatedComment = postCommentRepository.findById(savedPostComment.getId()).get();
		assertThat(updatedComment).isSameAs(returnComment);
	}
	
	@Test
	public void testPostCommentDelete() {
		//given
		User user = new User(name,email, picture, userTitle, introduce, Role.USER);
		userRepository.save(user);
		Post post = new Post(title, content, user, null);
		postRepository.save(post);
		PostComment postComment = new PostComment(post,user, content);
		PostComment savedPostComment = postCommentRepository.save(postComment);
		
		//when
		postCommentRepository.delete(savedPostComment);

		//then
		assertThrows(IllegalArgumentException.class,
				() -> postCommentRepository.findById(savedPostComment.getId()).orElseThrow(() -> new IllegalArgumentException()));
	}
}
