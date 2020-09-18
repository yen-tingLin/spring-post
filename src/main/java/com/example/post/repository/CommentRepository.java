package com.example.post.repository;

import java.util.List;

import com.example.post.model.Comment;
import com.example.post.model.Post;
import com.example.post.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findAllByPost(Post post);
    List<Comment> findAllByUser(User user);
}
