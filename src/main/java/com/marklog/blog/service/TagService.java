package com.marklog.blog.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.marklog.blog.domain.tag.TagRepository;
import com.marklog.blog.dto.TagCountResponseDto;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class TagService {
	private final TagRepository tagRepository;
}
