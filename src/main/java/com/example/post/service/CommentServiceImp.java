package com.example.post.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.post.dto.CommentDto;
import com.example.post.exception.SpringPostException;
import com.example.post.model.Comment;
import com.example.post.model.NotificationEmail;
import com.example.post.model.Post;
import com.example.post.model.User;
import com.example.post.repository.CommentRepository;
import com.example.post.repository.PostRepository;
import com.example.post.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImp implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;

    @Autowired
    CommentServiceImp(CommentRepository commentRepository, 
                PostRepository postRepository, 
                AuthService authService,
                MailContentBuilder mailContentBuilder, 
                MailService mailService,
                UserRepository userRepository)
    {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.authService = authService;
        this.mailContentBuilder = mailContentBuilder;
        this.mailService = mailService;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public void createComent(CommentDto commentDto) {
        Comment newComment = mapFromCommentDto(commentDto);
        // set comment with current user
        newComment.setUser(authService.getCurrentUser());

        commentRepository.save(newComment);

        // String mailMessage = mailContentBuilder
        // .builder(newComment.getPost().getUser().getUserName() + " post a comment on
        // your post " + POST_URL);

        // send email to notify that comment is published
        sendCommentNotification(newComment);

    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> getAllCommentForPost(Long postId) {
        // find post by postId
        Optional<Post> postOptional = postRepository.findById(postId);
        postOptional.orElseThrow(() -> 
                new SpringPostException("Post not found with id " + Long.toString(postId)));

        Post postFound = postOptional.get();
                        // List<Comment>
        List<Comment> commentList = commentRepository.findAllByPost(postFound);
                        // stream<Comment>
        return commentList.stream()
                        // stream<CommentDto>
                        .map(this::mapToCommentDto)
                        .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
	public List<CommentDto> getAllCommentForUserName(String userName) {
        Optional<User> userOptional = userRepository.findByUserName(userName);
        userOptional.orElseThrow(() -> 
                        new SpringPostException("User not found with name " + userName));
        
        User userFound = userOptional.get();
        List<Comment> commentList = commentRepository.findAllByUser(userFound);
       
        return commentList.stream()
                        .map(this::mapToCommentDto)
                        .collect(Collectors.toList()); 
	} 

    private void sendCommentNotification(Comment comment) {
        NotificationEmail notificationEmail = new NotificationEmail();
        notificationEmail.setSubject("Comments on yout post");
        notificationEmail.setRecipient(comment.getPost().getUser().getEmail());
        notificationEmail.setBody(comment.getUser().getUserName() + 
                            " commented on your post **" + comment.getPost().getTitle() + 
                            "** : " + comment.getText());

        mailService.sendMail(notificationEmail);
    }

    private Comment mapFromCommentDto(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setPublishDate(Instant.now());
        // find post by title and set comment with current user
        Optional<Post> postOptional = postRepository.findByTitle(commentDto.getPostTitle());
        postOptional.orElseThrow(() -> 
                new SpringPostException("Post not found with title " + commentDto.getPostTitle()));
        Post postFound = postOptional.get();
        comment.setPost(postFound);

        return comment;
    }

    public CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getCommentId());
        commentDto.setText(comment.getText());
        commentDto.setPostTitle(comment.getPost().getTitle());
        commentDto.setPostId(comment.getPost().getPostId());
        commentDto.setUserName(comment.getUser().getUserName());
        commentDto.setCreatedDate(comment.getPublishDate());

        return commentDto;
    }

    
}
