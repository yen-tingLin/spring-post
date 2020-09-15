package com.example.post.controller;

import com.example.post.dto.CommentDto;
import com.example.post.service.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }    

    @PostMapping("/create")
    public ResponseEntity<Void> createComment(@RequestBody CommentDto commentDto) {
        commentService.createComent(commentDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }



    
}
