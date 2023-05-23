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

import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.post.tag.PostTag;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;
import com.marklog.blog.dto.PostResponseDto;
import com.marklog.blog.dto.PostSaveRequestDto;
import com.marklog.blog.dto.PostUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final TagService tagService;
	private final PostTagService postTagService;

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
		Elements elements = doc.getElementsByTag("img");
		if (elements.size() < 1) {
			return null;
		} else {
			return elements.first().attr("src");
		}
	}

	private String ejectSummary(String html) {
		String summary = null;
		String text = htmlToText(html);

		String[] splitTexts = text.split("\n");
		for (String splitText : splitTexts) {
			if (splitText != "") {
				summary = splitText;
				if (summary.length() > 30) {
					summary = summary.substring(0, 30);
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

		Post post = new Post(thumbnail, summary, requestDto.getTitle(), requestDto.getContent(), user);
		post = postRepository.save(post);

		List<String> tagList = requestDto.getTagList();
		for(String tag:tagList) {
			Long tagId = tagService.save(tag);
			postTagService.save(post.getId(), tagId);
		}
		
		return post.getId();
	}

	public void update(Long id, PostUpdateRequestDto requestDto) {
		Post post = postRepository.findById(id).orElseThrow();
		post.update(requestDto.getTitle(), requestDto.getContent());

		List<String> tagList = requestDto.getTagList();
		List<PostTag> postTagList = post.getPostTags();
		for(int i=0;i<postTagList.size();i++) {
			postTagService.delete(postTagList.get(i).getId());
			post.getPostTags().remove(i);
		}
		
		for(String tag:tagList) {
			Long tagId = tagService.save(tag);
			postTagService.save(post.getId(), tagId);
		}
	}

	public PostResponseDto findById(Long id) {
		Post entity = postRepository.findById(id).orElseThrow();
		return new PostResponseDto(entity);
	}

	public void delete(Long id) {
		postRepository.deleteById(id);
	}
	
	public Page<PostResponseDto> recentPost(Pageable pageable) {
		Page<PostResponseDto> pagePostListResponseDto = postRepository.findAll(pageable)
				.map(PostResponseDto::new);
		return pagePostListResponseDto;
	}
}
