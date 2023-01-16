package com.marklog.blog.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.marklog.blog.web.dto.HelloRequestAndResponseDto;
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloControllerTest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void hello_return() throws Exception{
		String hello ="hello";
		

		String url = "http://localhost:"+port+"/hello";
		
		//when
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
		//then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isEqualTo(responseEntity);

	}
	@Test
	public void hello_response_return() throws Exception{
		//given
		String name ="hello";
		int amount = 1000;
		HelloRequestAndResponseDto dto = new HelloRequestAndResponseDto(name, amount);
		String url = "http://localhost:"+port+"/helloDto";

		//when
		ResponseEntity<HelloRequestAndResponseDto> responseEntity = restTemplate.postForEntity(url, dto, HelloRequestAndResponseDto.class);
		//then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isEqualTo(dto);

	}
}
