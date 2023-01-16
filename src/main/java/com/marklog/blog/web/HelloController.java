package com.marklog.blog.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marklog.blog.web.dto.HelloRequestAndResponseDto;

@RestController
public class HelloController {
	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}
	@GetMapping("/hello/dto")
	public HelloRequestAndResponseDto hello_dto(@RequestParam("name") String name, @RequestParam("amount") int amount) {
		return new HelloRequestAndResponseDto(name, amount);
	}
}
