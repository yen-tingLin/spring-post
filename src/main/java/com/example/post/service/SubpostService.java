package com.example.post.service;

import java.util.List;

import com.example.post.dto.SubpostDto;


public interface SubpostService {

    SubpostDto create(SubpostDto subpostDto);
	List<SubpostDto> getAllSubposts();
	SubpostDto getSingleSubpost(Long id);

    
}
