package com.example.post.service;

import java.util.Optional;

import com.example.post.dto.VoteDto;
import com.example.post.exception.SpringPostException;
import com.example.post.model.Post;
import com.example.post.model.Vote;
import com.example.post.repository.PostRepository;
import com.example.post.repository.VoteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.post.model.VoteType.LIKE;

@Service
public class VoteServiceImp implements VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    @Autowired
    VoteServiceImp(VoteRepository voteRepository,
                PostRepository postRepository,
                AuthService authService) 
    {
        this.voteRepository = voteRepository;
        this.postRepository = postRepository;
        this.authService = authService;
    }

    @Transactional
    @Override
    public void vote(VoteDto voteDto) {
        Optional<Post> postOptional = postRepository.findById(voteDto.getPostId());
        postOptional.orElseThrow(() -> 
                new SpringPostException("Post not found with id " + Long.valueOf(voteDto.getPostId())));     
        
        Post postFound = postOptional.get();
        Optional<Vote> voteByPostAndUser = 
                voteRepository.findTopByPostAndUserOrderByVoteIdDesc(
                                    postFound, authService.getCurrentUser());
        if(voteByPostAndUser.isPresent() && 
                voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
            throw new SpringPostException("You have already "+
                        voteDto.getVoteType() + "d for this post");
        }
        if(LIKE.equals(voteDto.getVoteType())) {
            postFound.setVoteCount(postFound.getVoteCount() + 1);
        } else {
            postFound.setVoteCount(postFound.getVoteCount() - 1);
        }

        Vote newVote = mapFromVoteDto(voteDto, postFound);
        voteRepository.save(newVote);
        postRepository.save(postFound);
    }

    private Vote mapFromVoteDto(VoteDto voteDto, Post post) {
        Vote vote = new Vote();
        vote.setPost(post);
        vote.setVoteType(voteDto.getVoteType());
        vote.setUser(authService.getCurrentUser());
        
        return vote;        
    }
    
}
