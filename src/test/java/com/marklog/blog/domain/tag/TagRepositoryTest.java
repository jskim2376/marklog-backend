//package com.marklog.blog.domain.tag;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.marklog.blog.domain.post.Post;
//import com.marklog.blog.domain.post.PostRepository;
//import com.marklog.blog.domain.user.Role;
//import com.marklog.blog.domain.user.User;
//import com.marklog.blog.domain.user.UserRepository;
//import com.marklog.blog.dto.TagCountResponseDto;
//
//@Transactional
//@AutoConfigureTestDatabase(replace = Replace.NONE)
//@DataJpaTest
//public class TagRepositoryTest {
//	@Autowired
//	UserRepository userRepository;
//	@Autowired
//	PostRepository postRepository;
//	User user;
//	Post post;
//
//	@Autowired
//	TagRepository tagRepository;
//	String tagName = "new tag";
//
//	@BeforeEach
//	public void setupEach() {
//		String name = "name";
//		String email = "test@gmail.com";
//		String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
//		String userTitle = "myblog";
//		String introduce = "introduce";
//		user = new User(name, email, picture, userTitle, introduce, Role.USER);
//		userRepository.save(user);
//
//		String thumbnail = "thumbnail";
//		String summary = "summary";
//		String title = "title";
//		String content = "title";
//		post = new Post(thumbnail, summary, title, content, user);
//		postRepository.save(post);
//	}
//
//	public Tag createTag() {
//		Tag tag = new Tag(post, tagName);
//		return tagRepository.save(tag);
//	}
//
//	public Tag createTag(String tagName) {
//		Tag tag = new Tag(post, tagName);
//		return tagRepository.save(tag);
//	}
//
//	@Test
//	public void testTagSave() {
//		// given
//		// when
//		Tag tag = createTag();
//
//		// then
//		assertThat(tag.getId()).isGreaterThan(0);
//	}
//
//	@Test
//	public void testTagDelete() {
//		// given
//		Tag tag = createTag();
//
//		// when
//		tagRepository.delete(tag);
//
//		// then
//		assertThrows(IllegalArgumentException.class,
//				() -> tagRepository.findById(tag.getId()).orElseThrow(() -> new IllegalArgumentException()));
//	}
//
//	@Test
//	public void testCountTagNameByUserId() {
//		// given
//		createTag();
//		createTag();
//		String testTagName = "hallo";
//		createTag(testTagName);
//		createTag(testTagName);
//		createTag(testTagName);
//
//		// when
//		List<TagCountResponseDto>tagCountResponseDtos = tagRepository.countTagNameByUserId(user.getId());
//
//		// then
//		assertThat(tagCountResponseDtos.get(0).getCount()).isEqualTo(3);
//		assertThat(tagCountResponseDtos.get(0).getName()).isEqualTo(testTagName);
//		assertThat(tagCountResponseDtos.get(1).getCount()).isEqualTo(2);
//		assertThat(tagCountResponseDtos.get(1).getName()).isEqualTo(tagName);
//	}
//
//	@Test
//	public void testCountTagNameByUserINod() {
//		// given
//		createTag();
//		createTag();
//		String testTagName = "hallo";
//		createTag(testTagName);
//		createTag(testTagName);
//		createTag(testTagName);
//
//		// when
//		List<TagCountResponseDto>tagCountResponseDtos = tagRepository.countTagNameByUserId(2L);
//
//		// then
//		assertThat(tagCountResponseDtos.size()).isEqualTo(0);
//	}
//}
