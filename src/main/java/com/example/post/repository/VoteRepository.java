package com.example.post.repository;

import java.util.Optional;

import com.example.post.model.Post;
import com.example.post.model.User;
import com.example.post.model.Vote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(
                    Post post, User currentUser);
    
}
