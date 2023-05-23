package com.marklog.blog.service;

import org.springframework.stereotype.Service;

import com.marklog.blog.domain.tag.Tag;
import com.marklog.blog.domain.tag.TagRepository;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class TagService {
	private final TagRepository tagRepository;
	Long save(String tagName){
		try {
			return tagRepository.findByName(tagName).orElseThrow().getId();
		}catch(Exception e){
			return tagRepository.save(new Tag(tagName)).getId();
		}
	}
	
}
