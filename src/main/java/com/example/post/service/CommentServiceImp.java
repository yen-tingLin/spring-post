package com.example.post.service;

import java.time.Instant;
import java.util.Optional;

import com.example.post.dto.CommentDto;
import com.example.post.exception.SpringPostException;
import com.example.post.model.Comment;
import com.example.post.model.NotificationEmail;
import com.example.post.model.Post;
import com.example.post.repository.CommentRepository;
import com.example.post.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImp implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final AuthService authService;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;

    @Autowired
    CommentServiceImp(
        CommentRepository commentRepository, 
        PostRepository postRepository, 
        AuthService authService,
        MailContentBuilder mailContentBuilder,
        MailService mailService) 
    {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.authService = authService;
        this.mailContentBuilder = mailContentBuilder;
        this.mailService = mailService;
    }

    @Transactional
    @Override
    public void createComent(CommentDto commentDto) {
        Comment newComment = mapFromCommentDto(commentDto);
        // set comment with current user
        newComment.setUser(authService.getCurrentUser());

        commentRepository.save(newComment);

        
        // String mailMessage = mailContentBuilder
        //         .builder(newComment.getPost().getUser().getUserName() + " post a comment on your post " + POST_URL);
        
        // send email to notify that comment is published
        sendCommentNotification(newComment);

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

    
}
