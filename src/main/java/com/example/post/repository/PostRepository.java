package com.example.post.repository;

import java.util.List;

import com.example.post.model.Post;
import com.example.post.model.Subpost;
import com.example.post.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findBySubpost(Subpost subpost);
    List<Post> findByUser(User user);
  
}
