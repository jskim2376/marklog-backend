package com.marklog.blog.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marklog.blog.config.auth.JwtTokenProvider;
import com.marklog.blog.dto.TestUserResponseDto;
import com.marklog.blog.web.dto.PostUpdateRequestDto;
import com.marklog.blog.web.dto.UserResponseDto;
import com.marklog.blog.web.dto.UserUpdateRequestDto;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTest {
	@LocalServerPort
	private int port;
	@Autowired
	UserRepository userRepository;
	@Autowired
	JwtTokenProvider jwtTokenProvider;

	WebClient wc;
	ObjectMapper objectMapper;
	String accessTokenUser;
	String accessTokenSub;
	String accessTokenAdmin;
	
	String name = "name";
	String email = "test@gmail.com";
	String picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
	String title = "title";
	String introduce = "introduce";

	String postTitle = "post title";
	String postContent = "post content";
	String uri = "/api/v1/user/";

	@BeforeAll
	public void setUp() {
		wc = WebClient.create("http://localhost:" + port);
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		User userSub = new User(name, 2 + email, picture, title, introduce, Role.USER);
		userRepository.save(userSub);
		accessTokenSub = jwtTokenProvider.createAccessToken(userSub.getId(), email);

		User userAdmin = new User(name, 3 + email, picture, title, introduce, Role.ADMIN);
		userRepository.save(userAdmin);
		accessTokenAdmin = jwtTokenProvider.createAccessToken(userAdmin.getId(), email);
	}

	public Long createUser(String emailName) {
		User user = new User(name, emailName+email, picture, title, introduce, Role.USER);
		try {
			user = userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("존재하지않는 email입니다."));
		}catch(IllegalArgumentException e){
			userRepository.save(user);
		}
		accessTokenUser = jwtTokenProvider.createAccessToken(user.getId(), email);
		return user.getId();
	}
	
	
	@Test
	public void testGetAllUser() throws JsonMappingException, JsonProcessingException, JSONException {
		// given
		Long id = createUser("testAllGetUser");

		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri)
				.header("Authorization", "Bearer " + accessTokenAdmin)
				.attribute("page", 0)
				.attribute("size", 20)
				.retrieve().toEntity(String.class).block();

		// then-ready
		JSONObject jsonObject = new JSONObject(responseEntity.getBody());
		String getName = jsonObject.getJSONArray("content").getJSONObject(0).getString("name");
		Long size = jsonObject.getLong("size");
		
//		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(size).isEqualTo(20);
		assertThat(getName).isEqualTo(name);
	}
	

	@Test
	public void testGetUser() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createUser("testGetUser");

		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri + id).retrieve().toEntity(String.class).block();

		// then-ready
		TestUserResponseDto testUserResponseDto = objectMapper.readValue(responseEntity.getBody(),
				TestUserResponseDto.class);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(testUserResponseDto.getCreatedDate()).isEqualTo(testUserResponseDto.getModifiedDate());
		assertThat(testUserResponseDto.getEmail()).isEqualTo("testGetUser"+email);
		assertThat(testUserResponseDto.getName()).isEqualTo(name);
		assertThat(testUserResponseDto.getPicture()).isEqualTo(picture);
		assertThat(testUserResponseDto.getTitle()).isEqualTo(title);
		assertThat(testUserResponseDto.getIntroduce()).isEqualTo(introduce);
	}

	@Test
	public void testGetPost_user가_없을때() throws JsonMappingException, JsonProcessingException {
		// given
		// when
		ResponseEntity<String> responseEntity = wc.get().uri(uri + 0L)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testPutUser() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createUser("testPutUser");
		UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(name+2, picture+2, title+2, introduce+2);
		// when
		ResponseEntity<String> putResponseEntity = wc.put().uri(uri + id)
				.header("Authorization", "Bearer " + accessTokenUser)
				.body(Mono.just(userUpdateRequestDto), PostUpdateRequestDto.class)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		
		//then-ready
		HttpHeaders header = putResponseEntity.getHeaders();

		// then
		assertThat(putResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(header.getLocation().toString()).isEqualTo(uri+id);
	}

	@Test
	public void testPutUser_해당하는_User가_없을때() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createUser("testPutUser_해당하는_User가_없을때");
		UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(name+2, picture+2, title+2, introduce+2);

		// when
		ResponseEntity<String> putResponseEntity = wc.put().uri(uri + 0L)
				.header("Authorization", "Bearer " + accessTokenAdmin)
				.body(Mono.just(userUpdateRequestDto), PostUpdateRequestDto.class)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(putResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testPutUser_인증이_없을때() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createUser("testPutUser_인증이_없을때");
		UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(name+2, picture+2, title+2, introduce+2);
		// when
		ResponseEntity<String> responseEntity = wc.put().uri(uri + id)
				.body(Mono.just(userUpdateRequestDto), PostUpdateRequestDto.class)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void testPutUser_본인소유가_아닐때() throws JsonMappingException, JsonProcessingException {
		// given
		Long id=createUser("testPutUser_본인소유가_아닐때");
		UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(name+2, picture+2, title+2, introduce+2);
		// when
		ResponseEntity<String> responseEntity = wc.put().uri(uri + id)
				.header("Authorization", "Bearer " + accessTokenSub)
				.body(Mono.just(userUpdateRequestDto), PostUpdateRequestDto.class)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void testPutUser_ADMIN일때() throws JsonMappingException, JsonProcessingException {
		//given
		Long id=createUser("testPutUser_ADMIN일때");
		UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(name+2, picture+2, title+2, introduce+2);
		// when
		ResponseEntity<String> responseEntity = wc.put().uri(uri + id)
				.header("Authorization", "Bearer " + accessTokenAdmin)
				.body(Mono.just(userUpdateRequestDto), PostUpdateRequestDto.class)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		
		//then-ready
		HttpHeaders header = responseEntity.getHeaders();

		// then
		assertThat(header.getLocation().toString()).isEqualTo(uri+id);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void testDeleteUser() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createUser("testDeleteUser");

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri + id)
				.header("Authorization", "Bearer " + accessTokenUser)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		ResponseEntity<String> responseEntity2 = wc.get().uri(uri + id)
				.header("Authorization", "Bearer " + accessTokenUser)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testDeleteUser_유저가_없을때() throws JsonMappingException, JsonProcessingException {
		// given
		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri + 0L)
				.header("Authorization", "Bearer " + accessTokenAdmin)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testDeleteUser_인증이_없을때() throws JsonMappingException, JsonProcessingException {
		Long id = createUser("testDeleteUser_인증이_없을때");

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri + id)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();
		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void testDeleteUser_본인소유가_아닐때() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createUser("testDeleteUser_본인소유가_아닐때");

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri + id)
				.header("Authorization", "Bearer " + accessTokenSub)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void testDeleteUser_ADMIN일때() throws JsonMappingException, JsonProcessingException {
		// given
		Long id = createUser("testDeleteUser_ADMIN일때");

		// when
		ResponseEntity<String> responseEntity = wc.delete().uri(uri + id)
				.header("Authorization", "Bearer " + accessTokenAdmin)
				.exchangeToMono(clientResponse -> clientResponse.toEntity(String.class)).block();

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

}