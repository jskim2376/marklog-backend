package com.marklog.blog.domain.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklog.blog.web.dto.UserUpdateRequestDto;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class UserTest {
	@Autowired
	MockMvc mvc;
	@Autowired
	UsersRepository usersRepository;
    @PersistenceContext
    EntityManager entityManager;

	String name;
	String email;
	String picture;
	String title;
	String introduce;

	@BeforeEach
	public void setUp() {
		name = "name";
		email = "test@gmail.com";
		picture = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		title = "title";
		introduce = "introduce";

        Users user =  Users.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .title(title)
                .introduce(introduce)
                .role(Role.USER)
                .build();
        user = usersRepository.save(user);
}

    @AfterEach
    public void teardown() {
    	usersRepository.deleteAll();
    	entityManager
            .createNativeQuery("ALTER TABLE users ALTER COLUMN `id` RESTART WITH 1")
            .executeUpdate();
    }


	@Test
	public void testUserGet() throws Exception {
		//given
		String path = "/v1/user/1";

		//when
		//then
		mvc.perform(get(path))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.email").value(email))
		.andExpect(jsonPath("$.name").value(name))
		.andExpect(jsonPath("$.picture").value(picture))
		.andExpect(jsonPath("$.title").value(title))
		.andExpect(jsonPath("$.introduce").value(introduce));
	}

	@WithMockUser(roles= {"ADMIN"})
	@Test
	public void testUserPut() throws Exception {
		//given
		String path = "/v1/user/1";
		String putName = "name2";
		String putPicture = "http://upload.wikimedia.org/wikipedia/commons/thumb/6/69/How_to_use_icon.svg/40px-How_to_use_icon.svg.png";
		String putTitle = "title2";
		String putIntroduce = "hello world";

		UserUpdateRequestDto updateRequestDto = UserUpdateRequestDto.builder().name(putName).picture(putPicture).title(putTitle).introduce(putIntroduce).build();

		//when
		mvc.perform(put(path)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(updateRequestDto)))
		.andExpect(status().isOk());

		//then
		mvc.perform(get(path))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name").value(putName))
		.andExpect(jsonPath("$.picture").value(putPicture))
		.andExpect(jsonPath("$.title").value(putTitle))
		.andExpect(jsonPath("$.introduce").value(putIntroduce));
	}

	@WithMockUser(roles= {"ADMIN"})
	@Test
	public void testUserDelete() throws Exception {
		//given
		String path = "/v1/user/1";

		//when
		mvc.perform(delete(path))
		.andExpect(status().isNoContent());

		//then
		mvc.perform(get(path))
		.andExpect(status().isBadRequest());
	}

	public static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
}
