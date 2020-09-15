package com.example.post.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.post.dto.PostRequest;
import com.example.post.dto.PostResponse;
import com.example.post.exception.SpringPostException;
import com.example.post.model.Post;
import com.example.post.model.Subpost;
import com.example.post.model.User;
import com.example.post.repository.PostRepository;
import com.example.post.repository.SubpostRepository;
import com.example.post.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PostServiceImp implements PostService {

    private final PostRepository postRepository;
    private final SubpostRepository subpostRepository;
    private final AuthService authService;


    @Autowired
    public PostServiceImp(
        PostRepository postRepository,
        SubpostRepository subpostRepository,
        AuthService authService) 
    {
        this.postRepository = postRepository;
        this.subpostRepository = subpostRepository;
        this.authService = authService;
    }

    @Transactional
    @Override
    public void create(PostRequest postRequest) {
 
        Post newPost = mapFromPostRequest(postRequest);
        User currentUser = authService.getCurrentUser();

        newPost.setUser(currentUser);
        postRepository.save(newPost);

        // postRequest.setId(postSaved.getPostId());
        // // test
        // System.out.println("postRequest id " + postRequest.getId());

    }

    @Transactional(readOnly = true)
    @Override
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                        .stream()
                        .map(this::mapToPostResponse)
                        .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    @Override
    public PostResponse getSinglePost(Long id) {
        Optional<Post> postOptional = postRepository.findById(id);
        postOptional.orElseThrow(() -> 
                    new SpringPostException("Post not found with id " + Long.toString(id)));

        Post postFound = postOptional.get();
        PostResponse postResponse = mapToPostResponse(postFound);
        return postResponse;
    }

    @Transactional(readOnly = true)
    @Override
    public List<PostResponse> getPostBySubpost(Long subpostId) {
        // get subpost by id
        Optional<Subpost> subpostOptional = subpostRepository.findById(subpostId);
        subpostOptional.orElseThrow(() -> 
                        new SpringPostException("Subpost not found with id " + Long.toString(subpostId)));
        // get post list by subpost
        Subpost subpostFound = subpostOptional.get();
        List<Post> postList = postRepository.findBySubpost(subpostFound);     
        return postList.stream()
                        .map(this::mapToPostResponse)
                        .collect(Collectors.toList());
    }

    // @Transactional(readOnly = true)
    // @Override
    // public List<PostResponse> getPostByUserName(String userName) {
    //     // get user object by userName
    //     Optional<User> userOptional = userRepository.findByUserName(userName);
    //     userOptional.orElseThrow(() -> new UsernameNotFoundException(userName));
    //     // get post list by user object
    //     User userFound = userOptional.get();
    //     List<Post> postListByUser = postRepository.findByUser(userFound);
    //     return postListByUser.stream()
    //                         .map(this::mapToPostResponse)
    //                         .collect(Collectors.toList());
    // }


    private Post mapFromPostRequest(PostRequest postRequest) {
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setDescription(postRequest.getDescription());
        post.setPublishDate(Instant.now());

        // find subpost by category
        String category = postRequest.getSubpostName();
        Optional<Subpost> subpostOptional = subpostRepository.findByCategory(category);
        subpostOptional.orElseThrow(() -> 
                        new SpringPostException("Subpost not found with name " + category));
        // set subpost in post
        Subpost subpostFound = subpostOptional.get();
        post.setSubpost(subpostFound);

        return post;
    }

    public PostResponse mapToPostResponse(Post post) {
        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getPostId());
        postResponse.setPostTitle(post.getTitle());
        postResponse.setCategory(post.getSubpost().getCategory());
        postResponse.setDescription(post.getDescription());
        postResponse.setUserName(post.getUser().getUserName());
        postResponse.setUrl(post.getUrl());

        return postResponse;
    }

}
