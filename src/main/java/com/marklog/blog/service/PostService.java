package com.marklog.blog.service;

import java.util.List;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marklog.blog.controller.dto.PostListResponseDto;
import com.marklog.blog.controller.dto.PostResponseDto;
import com.marklog.blog.controller.dto.PostSaveRequestDto;
import com.marklog.blog.controller.dto.PostUpdateRequestDto;
import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.post.QPost;
import com.marklog.blog.domain.tag.Tag;
import com.marklog.blog.domain.tag.TagRepository;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final TagRepository tagRepository;
	
    private String markdownToHtml(String markdown) {
    	Parser parser = Parser.builder().build();
    	Node document = parser.parse(markdown);
    	HtmlRenderer renderer = HtmlRenderer.builder().build();
    	return renderer.render(document); 
    }
    
    private String htmlToText(String html) {
        HtmlToPlainText formatter = new HtmlToPlainText();
        String converted = formatter.getPlainText(Jsoup.parse(html));
        return converted;
    }
    
    
    private String ejectThumbnail(String html) {
    	Document doc = Jsoup.parse(html);
    	Elements elements =  doc.getElementsByTag("img");
    	if(elements.size()<1) {
    		return null;
    	}else {
    		return elements.first().attr("src");
    	}
    }
    private String ejectSummary(String html) {
    	String summary = null;
    	String text = htmlToText(html);

    	String[] splitTexts = text.split("\n");
		for(String splitText:splitTexts) {
			if(splitText != "") {
				summary = splitText;
				if(summary.length() > 30) {
					summary = summary.substring(0,30);
				}
				break;
			}
		}

		return summary;
    }
    
	public Long save(Long userId, PostSaveRequestDto requestDto) {
		User user = userRepository.getReferenceById(userId);
		
		String html = markdownToHtml(requestDto.getContent());
		String thumbnail = ejectThumbnail(html);
		String summary = ejectSummary(html);
		
		Post post = new Post(thumbnail, summary, requestDto.getTitle(),requestDto.getContent(), user, null);
		post = postRepository.save(post);

		List<String> tagNames = requestDto.getTagList();
		if(tagNames != null) {
			for(String tagName: tagNames) {
				Tag tag = Tag.builder().name(tagName).post(post).build();
				tagRepository.save(tag);
			}
		}
		return post.getId();
	}



	public void update(Long id, PostUpdateRequestDto requestDto) {
		Post post = postRepository.findById(id).orElseThrow();
		post.update(requestDto.getTitle(), requestDto.getContent());

		List<Tag> tags = tagRepository.findByPost(post);
		for(Tag tag : tags) {
			tagRepository.delete(tag);
		}
		List<String> tagNames = requestDto.getTagList();
		if(tagNames != null) {
			for(String tagName: tagNames) {
				tagRepository.save(Tag.builder().name(tagName).post(post).build());
			}
		}
	}

	public PostResponseDto findById(Long id) {
		Post entity = postRepository.findById(id).orElseThrow();
		return new PostResponseDto(entity);
	}

	public void delete(Long id) {
		postRepository.deleteById(id);
	}
	
	public Page<PostResponseDto> search(Pageable pageable, String[] keywords){
		QPost qpost = QPost.post;
		
		BooleanExpression predicate = null;
		for(String keyword:keywords) {
			if(predicate==null) {
				predicate = qpost.content.containsIgnoreCase(keyword).or(qpost.title.containsIgnoreCase(keyword));
			}
			else {
				predicate = predicate.or(qpost.content.containsIgnoreCase(keyword)).or(qpost.title.containsIgnoreCase(keyword));
			}
		}
		
		
		// when
		Page<PostResponseDto> page = postRepository.findAll(predicate, pageable).map(PostResponseDto::toDto);
		return page;
	}

}
