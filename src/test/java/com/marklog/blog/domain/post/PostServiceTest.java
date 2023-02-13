package com.marklog.blog.domain.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.marklog.blog.domain.tag.Tag;
import com.marklog.blog.domain.tag.TagRepository;
import com.marklog.blog.domain.user.Role;
import com.marklog.blog.domain.user.Users;
import com.marklog.blog.domain.user.UsersRepository;
import com.marklog.blog.service.PostService;
import com.marklog.blog.web.dto.PostResponseDto;
import com.marklog.blog.web.dto.PostSaveRequestDto;
import com.marklog.blog.web.dto.PostUpdateRequestDto;


@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
	@Mock
	UsersRepository userRepository;

	@Mock
	PostRepository postRepository;

	@Mock
	TagRepository tagRepository;

	PostService postService;

	Long id = 1L;
	String title="title";
	String content="title";

	String name = "name";
	String email = "test@gmail.com";
	String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
	String userTitle = "myblog";
	String introduce = "introduce";

	@Test
	public void testSavePostService() {
		//given
		Users user = new Users(name, email, picture, title, introduce, Role.USER);
		when(userRepository.getReferenceById(id)).thenReturn(user);

		Post post = spy(new Post(title, content, user));
		when(post.getId()).thenReturn(id);
		when(postRepository.save(any())).thenReturn(post);

		List<String> tagList = new ArrayList<>();
		tagList.add("java");
		tagList.add("testTag");

		postService = new PostService(postRepository, userRepository, tagRepository);
		PostSaveRequestDto postSaveRequestDto = new PostSaveRequestDto(title, content, id, tagList);

		//when
		Long id = postService.save(postSaveRequestDto);

		//then
		assertThat(id).isGreaterThan(0L);
	}



	@Test
	public void testFindByIdPostService() {
		//given
		Users user = new Users(name, email, picture, title, introduce, Role.USER);
		Post post = new Post(title, content, user);
		Optional<Post> optionalPost = Optional.of(post);
		when(postRepository.findById(any())).thenReturn(optionalPost);

		PostResponseDto postResponseDto =  new PostResponseDto(post);
		postService = new PostService(postRepository, userRepository, tagRepository);

		//when
		PostResponseDto postResponseDtoFound =  postService.findById(id);

		//then
		assertThat(postResponseDtoFound).isEqualTo(postResponseDto);
	}


	@Test
	public void testUpdatePostervice() {
		//given
		Users user = new Users(name, email, picture, title, introduce, Role.USER);
		Post post = new Post(title, content, user);
		Optional<Post> optionalPost = Optional.of(post);
		when(postRepository.findById(any())).thenReturn(optionalPost);

		List<Tag> tagList = new ArrayList<>();
		tagList.add(new Tag(post, "updateTag1"));
		tagList.add(new Tag(post, "updateTag2"));
		when(tagRepository.findByPost(any())).thenReturn(tagList);

		List<String> tagListRequest = new ArrayList<>();
		tagListRequest.add("newTag1");
		tagListRequest.add("newtag2");
		tagListRequest.add("newtag3");

		postService = new PostService(postRepository, userRepository, tagRepository);

		PostUpdateRequestDto postUpdateRequestDto = new PostUpdateRequestDto(title+'2', content+"2", tagListRequest);

		//when
		Long getId = postService.update(id, postUpdateRequestDto);

		//then
		verify(tagRepository, times(2)).delete(any());
		verify(tagRepository, times(3)).save(any());
		assertThat(getId).isEqualTo(id);
	}

	@Test
	public void testDeletePostService() {
		//given
		postService = new PostService(postRepository, userRepository, tagRepository);

		//when
		postService.delete(id);

		//then
		verify(postRepository).deleteById(id);
	}

}
