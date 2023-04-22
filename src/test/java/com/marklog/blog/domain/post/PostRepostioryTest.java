package com.marklog.blog.domain.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.marklog.blog.domain.post.like.PostLikeRepository;
import com.marklog.blog.domain.tag.Tag;
import com.marklog.blog.domain.tag.TagRepository;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;
import com.marklog.blog.dto.PostResponseDto;
import com.querydsl.core.types.dsl.BooleanExpression;
@Transactional
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class PostRepostioryTest {
	@Autowired
	UserRepository userRepository;
	User user;
	User user2;

	@Autowired
	PostRepository postRepository;
	@Autowired
	PostLikeRepository postLikeRepository;
	@Autowired
	TagRepository tagRepository;

	String thumbnail = "hi";
	String sumary = "content";
	String title = "title";
	String content = "content";
	List<Tag> tags;
	String tagName1 = "tag1";
	String tagName2 = "tag2";

	@BeforeEach
	public void setUpEach() {
		String name = "name";
		String email = "testPostRepostiroy@gmail.com";
		String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String userTitle = "myblog";
		String introduce = "introduce";
		user = new User(name, email, picture, userTitle, introduce, Role.USER);
		userRepository.save(user);

		user2 = new User(name, 2+email, picture, userTitle, introduce, Role.USER);
		userRepository.save(user2);

	}

	public Post createPost() {
		Post post = new Post(thumbnail, sumary, title, content, user);
		post = postRepository.save(post);

		Tag tag1 = new Tag(post, tagName1);
		tagRepository.saveAndFlush(tag1);
		Tag tag2 = new Tag(post, tagName2);
		tagRepository.saveAndFlush(tag2);
		return post;
	}
	
	public Post createPostUser2() {
		Post post = new Post(thumbnail, sumary, title, content, user2);
		post = postRepository.save(post);

		Tag tag1 = new Tag(post, tagName1);
		tagRepository.saveAndFlush(tag1);
		Tag tag2 = new Tag(post, tagName2);
		tagRepository.saveAndFlush(tag2);
		return post;
	}

	public Post createPostNoTag() {
		Post post = new Post(thumbnail, sumary, title, content, user);
		postRepository.save(post);
		return post;
	}

	@Test
	public void testFindAllPostRepository_recent_post() {
		// given
		postRepository.deleteAll();
		createPost();
		createPost();

		Sort sort = Sort.by("id").descending();
		PageRequest pageRequest = PageRequest.of(0, 4, sort);

		// when
		Page<Post> page = postRepository.findAll(pageRequest);

		// then-ready
		List<Post> postList = page.getContent();
		Post post = postList.get(0);

		// then
		assertThat(post.getThumbnail()).isEqualTo(thumbnail);
		assertThat(post.getSummary()).isEqualTo(sumary);
		assertThat(post.getTitle()).isEqualTo(title);
		assertThat(post.getContent()).isEqualTo(content);
		assertThat(page.getTotalElements()).isEqualTo(2);
		assertThat(page.getTotalPages()).isEqualTo(1);
	}

	@Test
	public void testFindAllTitleContentPostRepository_by_content() throws JsonProcessingException {
		// given
		String newContent = "qwertyasdf";
		Post post = new Post(thumbnail, sumary, title, newContent, user);
		postRepository.save(post);

		String searchText = newContent;
		BooleanExpression predicate = QPost.post.content.containsIgnoreCase(searchText)
				.or(QPost.post.title.containsIgnoreCase(searchText));
		PageRequest pageRequest = PageRequest.of(0, 4);

		// when
		Page<Post> page = postRepository.findAll(predicate, pageRequest);

		// then-ready
		List<Post> postList = page.getContent();
		PostResponseDto postDto = new PostResponseDto(postList.get(0));

		// then
		assertThat(postList.size()).isEqualTo(1);
		assertThat(postDto.getContent()).isEqualTo(searchText);
	}

	@Test
	public void testFindAllTitleContentPostRepository_by_title() throws JsonProcessingException {
		// given
		String newTitle = "qwertyasdf";
		Post post = new Post(thumbnail, sumary, newTitle, content, user);
		postRepository.save(post);

		String searchText = newTitle;
		BooleanExpression predicate = QPost.post.content.containsIgnoreCase(searchText)
				.or(QPost.post.title.containsIgnoreCase(searchText));
		PageRequest pageRequest = PageRequest.of(0, 4);

		// when
		Page<Post> page = postRepository.findAll(predicate, pageRequest);

		// then-ready
		List<Post> postList = page.getContent();
		PostResponseDto postDto = new PostResponseDto(postList.get(0));

		// then
		assertThat(postList.size()).isEqualTo(1);
		assertThat(postDto.getTitle()).isEqualTo(searchText);
	}

	@Test
	public void testFindAllTitleContentPostRepository_multi_search() throws JsonProcessingException {
		// given
		postRepository.deleteAll();
		createPost();
		createPost();

		String searchText = title;
		BooleanExpression predicate = QPost.post.content.containsIgnoreCase(searchText)
				.or(QPost.post.title.containsIgnoreCase(searchText));
		PageRequest pageRequest = PageRequest.of(0, 4);
		// when
		Page<Post> page = postRepository.findAll(predicate, pageRequest);

		// then-ready
		List<Post> postList = page.getContent();
		PostResponseDto postDto = new PostResponseDto(postList.get(0));

		// then
		assertThat(postList.size()).isEqualTo(2);
		assertThat(postDto.getTitle()).isEqualTo(searchText);
		assertThat(postList.size()).isGreaterThan(1);
	}

	@Test
	public void testFindAllTitleContentPostRepository_multi_keyword() throws JsonProcessingException {
		// given
		String newTitle = "multi_keyword1";
		Post post = new Post(thumbnail, sumary, newTitle, content, user);
		postRepository.save(post);
		String newTitle2 = "multi_keyword2";
		Post post2 = new Post(thumbnail, sumary, newTitle2, content, user);
		postRepository.save(post2);

		String searchText = newTitle;
		String searchText2 = newTitle2;
		BooleanExpression predicate = QPost.post.content.containsIgnoreCase(searchText)
				.or(QPost.post.title.containsIgnoreCase(searchText));
		predicate = predicate.or(QPost.post.content.containsIgnoreCase(searchText2))
				.or(QPost.post.title.containsIgnoreCase(searchText2));
		PageRequest pageRequest = PageRequest.of(0, 4);

		// when
		Page<Post> page = postRepository.findAll(predicate, pageRequest);

		// then-ready
		List<Post> postList = page.getContent();
		PostResponseDto postDto1 = new PostResponseDto(postList.get(0));
		PostResponseDto postDto2 = new PostResponseDto(postList.get(1));

		// then
		assertThat(postList.size()).isEqualTo(2);
		assertThat(postDto1.getTitle()).isEqualTo(searchText);
		assertThat(postDto2.getTitle()).isEqualTo(searchText2);
	}

	@Test
	public void testFindAllByTagName() throws JsonProcessingException {
		// given
		createPost();
		createPost();
		createPostNoTag();
		PageRequest pageRequest = PageRequest.of(0, 4);

		// when
		List<Post> posts = postRepository.findAllByTagName(pageRequest, tagName1);

		// then-ready
		PostResponseDto postDto1 = new PostResponseDto(posts.get(0));
		PostResponseDto postDto2 = new PostResponseDto(posts.get(1));

		// then
		assertThat(posts.size()).isEqualTo(2);
		assertThat(postDto1.getTagList().get(0).getName()).isEqualTo(tagName1);
		assertThat(postDto2.getTagList().get(0).getName()).isEqualTo(tagName1);
	}
	
	@Test
	public void testFindAllByTagNameAndUserId() throws JsonProcessingException {
		// given
		createPost();
		createPost();
		createPostNoTag();
		createPostUser2();
		PageRequest pageRequest = PageRequest.of(0, 4);

		// when
		List<Post> posts = postRepository.findAllByTagNameAndUserId(pageRequest, tagName1, user.getId());

		// then-ready
		PostResponseDto postDto1 = new PostResponseDto(posts.get(0));
		PostResponseDto postDto2 = new PostResponseDto(posts.get(1));

		// then
		assertThat(posts.size()).isEqualTo(2);
		assertThat(postDto1.getTagList().get(0).getName()).isEqualTo(tagName1);
		assertThat(postDto2.getTagList().get(0).getName()).isEqualTo(tagName1);
	}

	@Test
	public void testSavePostRepository() {
		// given
		// when
		Post post = createPost();
		// then
		assertThat(post.getId()).isNotNull();
	}

	@Test
	public void testSavePostRepository_Users_없을때_테스트() {
		// given
		User emptyUser = new User();
		Post post = new Post(thumbnail, sumary, title, content, emptyUser);
		// when
		Executable save = () -> postRepository.save(post);
		// then
		assertThrows(InvalidDataAccessApiUsageException.class, save);
	}

	@Test
	public void testFindByIdPostRepostiroy() {
		// given
		Post post = createPost();
		// when
		Post findPost = postRepository.findById(post.getId()).get();

		// then
		assertThat(post).isSameAs(findPost);
		assertThat(findPost.getTags().get(0).getName()).isEqualTo(tagName1);
	}

	@Test
	public void textUpdatePostRepository() {
		// given
		Post post = createPost();
		String newTitle = "newTitle";
		String newContent = "newContent";

		// when
		post.update(newTitle, newContent);

		// then-ready
		Post findPost = postRepository.findById(post.getId()).get();

		// then
		assertThat(findPost.getTitle()).isEqualTo(newTitle);
		assertThat(findPost.getContent()).isEqualTo(newContent);
	}

	@Test
	public void testDeletePostReposeitory() {
		// given
		Post post = createPost();
		// when
		postRepository.delete(post);

		// then
		assertThrows(IllegalArgumentException.class,
				() -> postRepository.findById(post.getId()).orElseThrow(() -> new IllegalArgumentException()));

	}

}
