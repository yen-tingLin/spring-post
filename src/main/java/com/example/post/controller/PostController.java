package com.example.post.controller;

import java.util.List;

import com.example.post.dto.PostRequest;
import com.example.post.dto.PostResponse;
import com.example.post.service.PostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }
      

    @PostMapping("/create")
    public ResponseEntity<Void> createPost(@RequestBody PostRequest postRequest) {
        postService.create(postRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.status(HttpStatus.OK)
                        .body(postService.getAllPosts());
    }

    // @GetMapping("/{id}")
    // public ResponseEntity<PostResponse> getSinglePost(@PathVariable Long id) {
    //     return ResponseEntity.status(HttpStatus.OK)
    //                     .body(postService.getSinglePost(id));       
    // }

    // // get post by category
    // @GetMapping("/by-subpost/{id}")
    // public ResponseEntity<List<PostResponse>> getPostBySubpost(@PathVariable Long id) {
    //     return ResponseEntity.status(HttpStatus.OK)
    //                     .body(postService.getPostBySubpost(id));            
    // }
    
    // @GetMapping("/by-user/{name}")
    // public ResponseEntity<List<PostResponse>> getPostByUserName(@PathVariable String userName) {
    //     return ResponseEntity.status(HttpStatus.OK)
    //                     .body(postService.getPostByUserName(userName)); 
    // } 


}
