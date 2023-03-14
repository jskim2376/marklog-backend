package com.marklog.blog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.tag.Tag;
import com.marklog.blog.domain.tag.TagRepository;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;
import com.marklog.blog.dto.PostListResponseDto;
import com.marklog.blog.dto.PostResponseDto;
import com.marklog.blog.dto.PostSaveRequestDto;
import com.marklog.blog.dto.PostUpdateRequestDto;
import com.querydsl.core.types.Predicate;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
	@Mock
	UserRepository userRepository;
	User user;
	Long userId = 2L;

	@Mock
	PostRepository postRepository;

	@Mock
	TagRepository tagRepository;

	PostService postService;
	Post post;
	Long postId = 1L;
	String thumbnail = "thumbnail";
	String summary = "summary";
	String title = "title";
	String content = "![](https://velog.velcdn.com/images/padomay1352/post/aa716ab1-e079-406b-ae82-c4489e7b95d1/image.png)\r\n"
			+ "# adsadasd as sa dsa dad ada s dsa\r\n"
			+ "hihihi thithithiad sad sa dasd sa dsad da a dsasasdsaa a sa sa saa sa  ad  ada\r\n"
			+ "asdad asd sa dsa dsa sad a dad  a  s as dsa dd sa da sa dsa sa dsa asd sa dsa\r\n";
	String tagName = "tagName";

	@BeforeEach
	public void setUp() {
		String name = "name";
		String email = "test@gmail.com";
		String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String userTitle = "myblog";
		String introduce = "introduce";
		user = new User(name, email, picture, userTitle, introduce, Role.USER);
		post = spy(new Post(thumbnail, summary, title, content, user));
		post.getTags().add(new Tag(post, tagName));
		postService = new PostService(postRepository, userRepository, tagRepository);
	}

	@Test
	public void testRescentPost() {
		// given
		List<Post> contents = new ArrayList<>();
		contents.add(post);
		int pageCount = 0;
		int size = 20;
		Pageable pageable = PageRequest.of(pageCount, size, Sort.by("id").descending());
		Page<Post> page = new PageImpl<>(contents, pageable, 1);
		when(postRepository.findAll(pageable)).thenReturn(page);

		// when
		Page<PostListResponseDto> postResponsePage = postService.recentPost(pageable);

		// then
		assertThat(postResponsePage.getContent().get(0).getTitle()).isEqualTo(title);
		assertThat(postResponsePage.getSize()).isEqualTo(size);
		assertThat(postResponsePage.getTotalElements()).isEqualTo(1);
	}

	@Test
	public void testSearchPostService() {
		// given
		List<Post> contents = new ArrayList<>();
		contents.add(post);
		int pageCount = 0;
		int size = 20;
		Pageable pageable = PageRequest.of(pageCount, size);
		Page<Post> page = new PageImpl<>(contents, pageable, 1);
		String[] keywords = { "content", "hello" };
		when(postRepository.findAll(any(Predicate.class), any(Pageable.class))).thenReturn(page);

		// when
		Page<PostListResponseDto> postResponsePage = postService.search(pageable, keywords);

		// then
		assertThat(postResponsePage.getContent().get(0).getTitle()).isEqualTo(title);
		assertThat(postResponsePage.getSize()).isEqualTo(size);
		assertThat(postResponsePage.getTotalElements()).isEqualTo(1);
	}

	@Test
	public void testFindAllByTagName() {
		// given
		int pageCount = 0;
		int size = 20;
		Pageable pageable = PageRequest.of(pageCount, size);
		
		List<Post> posts = new ArrayList<>();
		posts.add(post);
		
		when(postRepository.findAllByTagName(pageable, tagName)).thenReturn(posts);

		// when
		List<PostListResponseDto> postListResponseDtos = postService.findAllByTagName(pageable, tagName);
		PostListResponseDto postListResponseDto = postListResponseDtos.get(0);
		// then
		assertThat(postListResponseDtos.size()).isEqualTo(1);
		assertThat(postListResponseDto.getTitle()).isEqualTo(title);
		assertThat(postListResponseDto.getTagList().get(0).getName()).isEqualTo(tagName);
	}

	@Test
	public void testFindAllByTagNamAndeUserId() {
		// given
		int pageCount = 0;
		int size = 20;
		Pageable pageable = PageRequest.of(pageCount, size);
		
		List<Post> posts = new ArrayList<>();
		posts.add(post);
		when(postRepository.findAllByTagNameAndUserId(pageable, tagName,userId)).thenReturn(posts);

		// when
		List<PostListResponseDto> postListResponseDtos = postService.findAllByTagNameAndUserId(pageable, tagName, userId);
		PostListResponseDto postListResponseDto = postListResponseDtos.get(0);
		// then
		assertThat(postListResponseDtos.size()).isEqualTo(1);
		assertThat(postListResponseDto.getTitle()).isEqualTo(title);
		assertThat(postListResponseDto.getTagList().get(0).getName()).isEqualTo(tagName);
	}

	
	@Test
	public void testSavePostService() {
		//given
		when(userRepository.getReferenceById(anyLong())).thenReturn(user);
		when(postRepository.save(any())).thenAnswer(invocation -> {
			Post post = (Post)(invocation.getArguments()[0]);
			post = spy(post);
		    when(post.getId()).thenReturn(postId);
		    //when
		    assertThat(post.getThumbnail()).isEqualTo("https://velog.velcdn.com/images/padomay1352/post/aa716ab1-e079-406b-ae82-c4489e7b95d1/image.png");
		    assertThat(post.getSummary()).isEqualTo("adsadasd as sa dsa dad ada s d");
		    return post;
		});

		List<String> tagList = new ArrayList<>();
		tagList.add("java");
		tagList.add("testTag");
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(title, content, tagList);

		//when
		Long id = postService.save(1L,postSaveRequestDto);

		//then
		assertThat(id).isEqualTo(postId);
	}

	@Test
	public void testFindByIdPostService() {
		// given
		PostResponseDto postResponseDto = new PostResponseDto(post);
		Optional<Post> optionalPost = Optional.of(post);
		when(postRepository.findById(any())).thenReturn(optionalPost);

		// when
		PostResponseDto postResponseDtoFound = postService.findById(postId);

		// then
		assertThat(postResponseDtoFound).usingRecursiveComparison().isEqualTo(postResponseDto);

	}

	@Test
	public void testUpdatePostervice() {
		// given
		Optional<Post> optionalPost = Optional.of(post);
		when(postRepository.findById(any())).thenReturn(optionalPost);

		List<Tag> tagList = new ArrayList<>();
		tagList.add(new Tag(post, "updateTag1"));
		tagList.add(new Tag(post, "updateTag2"));
		when(tagRepository.findByPost(any())).thenReturn(tagList);

		List<String> tagListRequest = new ArrayList<>();
		tagListRequest.add("newTag1");
		tagListRequest.add("newtag2");

		PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(title + '2', content + "2",
				tagListRequest);

		// when
		postService.update(postId, postUpdateRequestDto);

		// then
		verify(postRepository).findById(postId);
		verify(post).update(postUpdateRequestDto.getTitle(), postUpdateRequestDto.getContent());
		verify(tagRepository).findByPost(any());
		verify(tagRepository, times(2)).delete(any());
	}

	@Test
	public void testDeletePostService() {
		// given
		// when
		postService.delete(postId);

		// then
		verify(postRepository).deleteById(postId);
	}
}
