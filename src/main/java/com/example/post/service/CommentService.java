package com.example.post.service;

import java.util.List;
import com.example.post.dto.CommentDto;


public interface CommentService {

	void createComent(CommentDto commentDto);
	List<CommentDto> getAllCommentForPost(Long postId);
    
}
