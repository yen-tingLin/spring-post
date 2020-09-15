package com.example.post.service;

import java.util.List;

import com.example.post.dto.PostRequest;
import com.example.post.dto.PostResponse;


public interface PostService {

    void create(PostRequest postDto);
	List<PostResponse> getAllPosts();
	PostResponse getSinglePost(Long id);
	List<PostResponse> getPostBySubpost(Long subpostId);
	//List<PostResponse> getPostByUserName(String userName);
    
}
