package com.marklog.blog.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marklog.blog.domain.notice.NoticeType;
import com.marklog.blog.domain.post.Post;
import com.marklog.blog.domain.post.PostRepository;
import com.marklog.blog.domain.post.comment.PostComment;
import com.marklog.blog.domain.post.comment.PostCommentRepository;
import com.marklog.blog.domain.user.User;
import com.marklog.blog.domain.user.UserRepository;
import com.marklog.blog.dto.PostCommentResponseDto;
import com.marklog.blog.dto.PostCommentSaveRequestDto;
import com.marklog.blog.dto.PostCommentUpdateRequestDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class PostCommentService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final PostCommentRepository postCommentRepository;
	private final NoticeService noticeService;

	public Long save(Long postId, Long userId, PostCommentSaveRequestDto requestDto) {
		Post post = postRepository.getReferenceById(postId);
		User user = userRepository.getReferenceById(userId);

		PostComment postComment;
		if (requestDto.getParentComment() == null) {
			postComment = new PostComment(post, user, requestDto.getContent());
		} else {
			postComment = new PostComment(post, user, requestDto.getContent());
			PostComment parentPostComment = postCommentRepository.getReferenceById(requestDto.getParentComment());
			postComment.setParent(parentPostComment);
		}
		postComment = postCommentRepository.save(postComment);

		NoticeType noticeType;
		String noticeContent;
		String url = "/post/"+post.getId();
		if(postComment.getParent() == null){
			noticeType = NoticeType.POST;
			noticeContent = "글 \'"+postComment.getContent()+"\' 에 새로운 댓글이 추가 되었습니다.";
			
		}else {
			noticeType = NoticeType.COMMENT;
			noticeContent = "댓글 \'"+postComment.getContent()+"\' 에 새로운 댓글이 추가 되었습니다.";
		}
		
		noticeService.save(userId, noticeType, noticeContent, url);
		
		return postComment.getId();
	}

	public List<PostCommentResponseDto> findAllByPostId(Long postId) {
		Post post = postRepository.getReferenceById(postId);
		List<PostComment> comments = postCommentRepository.findAllByPostAndParentIsNull(post);
		List<PostCommentResponseDto> postCommentResponseDtos = comments.stream().map(PostCommentResponseDto::toDto)
				.collect(Collectors.toList());
		return postCommentResponseDtos;
	}

	public PostCommentResponseDto findById(Long id) {
		PostComment postComment = postCommentRepository.findById(id).orElseThrow();
		PostCommentResponseDto postCommentResponseDto = PostCommentResponseDto.toDto(postComment);
		return postCommentResponseDto;
	}

	public void update(Long id, PostCommentUpdateRequestDto requestDto) {
		PostComment postComment = postCommentRepository.findById(id).orElseThrow();
		postComment.update(requestDto.getContent());
	}

	public void delete(Long id) {
		postCommentRepository.deleteById(id);
	}
}
