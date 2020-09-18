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
import com.example.post.repository.CommentRepository;
import com.example.post.repository.PostRepository;
import com.example.post.repository.SubpostRepository;
import com.example.post.repository.UserRepository;
import com.github.marlonlom.utilities.timeago.TimeAgo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class PostServiceImp implements PostService {

    private final PostRepository postRepository;
    private final SubpostRepository subpostRepository;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    @Autowired
    public PostServiceImp(
        PostRepository postRepository,
        SubpostRepository subpostRepository,
        AuthService authService,
        UserRepository userRepository,
        CommentRepository commentRepository) 
    {
        this.postRepository = postRepository;
        this.subpostRepository = subpostRepository;
        this.authService = authService;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    @Override
    public void create(PostRequest postRequest) {
 
        Post newPost = mapFromPostRequest(postRequest);
        User currentUser = authService.getCurrentUser();

        newPost.setUser(currentUser);
        postRepository.save(newPost);

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

        // test
        log.info("category id is " + subpostFound.getSubpostId());

        List<Post> postList = postRepository.findBySubpost(subpostFound);     
        return postList.stream()
                        .map(this::mapToPostResponse)
                        .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<PostResponse> getPostByUserName(String userName) {
        // get user object by userName
        Optional<User> userOptional = userRepository.findByUserName(userName);
        userOptional.orElseThrow(() -> new UsernameNotFoundException(userName));
        // get post list by user object
        User userFound = userOptional.get();
                            // List<Post>
        List<Post> postListByUser = postRepository.findByUser(userFound);
                            // stream<Post>
        return postListByUser.stream()
                            // stream<PostResponse>
                            .map(this::mapToPostResponse)
                            .collect(Collectors.toList());
    }


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
        post.setVoteCount(0);

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
        
        // set number of comments for the post
        Integer commentCount = commentRepository.findAllByPost(post).size();
        postResponse.setCommentCount(commentCount);

        // set vote count
        postResponse.setVoteCount(post.getVoteCount());

        // set duration
        String duration = TimeAgo.using(post.getPublishDate().toEpochMilli());
        postResponse.setDuration(duration);

        return postResponse;
    }

}
