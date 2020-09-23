package com.example.post.controller;

import java.util.List;

import com.example.post.dto.CommentDto;
import com.example.post.service.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(value = "http://localhost:4200")
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

    @GetMapping("/by-post/{postId}")
    public ResponseEntity<List<CommentDto>> getAllCommentForPost(@PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.OK)
                        .body(commentService.getAllCommentForPost(postId));
    }

    @GetMapping("/by-user/{userName}")
    public ResponseEntity<List<CommentDto>> getAllCommentForUserName(@PathVariable String userName) {
        return ResponseEntity.status(HttpStatus.OK)
                        .body(commentService.getAllCommentForUserName(userName));
    }


    
}
