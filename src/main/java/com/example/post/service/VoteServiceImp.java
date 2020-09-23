package com.example.post.service;

import java.util.Optional;

import com.example.post.dto.VoteDto;
import com.example.post.exception.SpringPostException;
import com.example.post.model.Post;
import com.example.post.model.User;
import com.example.post.model.Vote;
import com.example.post.repository.PostRepository;
import com.example.post.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Autowired
    VoteServiceImp(VoteRepository voteRepository,
                PostRepository postRepository, AuthService authService, 
                UserRepository userRepository) 
    {
        this.voteRepository = voteRepository;
        this.postRepository = postRepository;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public void vote(VoteDto voteDto) {

        // if(authService.isLoggedIn()) {
            Optional<Post> postOptional = postRepository.findById(voteDto.getPostId());
            postOptional.orElseThrow(() -> 
                    new SpringPostException("Post not found with id " + Long.valueOf(voteDto.getPostId())));     
            
            Post postFound = postOptional.get();
            // Optional<Vote> voteByPostAndUser = 
            //         voteRepository.findTopByPostAndUserOrderByVoteIdDesc(
            //                      postFound, authService.getCurrentUser());
    
            Optional<User> voterOptional = userRepository.findByUserName(voteDto.getUserName());
            voterOptional.orElseThrow(() -> 
                        new SpringPostException("User not found with name " + voteDto.getUserName()));
            User voterFound = voterOptional.get();
    
            Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(
                                        postFound, voterFound);                                    
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
    
            Vote newVote = mapFromVoteDto(voteDto, postFound, voterFound);
            voteRepository.save(newVote);
            postRepository.save(postFound);
        
        // } else {
        //     throw new SpringPostException("Cannot vote without logged in");
        // }

    }

    private Vote mapFromVoteDto(VoteDto voteDto, Post post, User user) {
        Vote vote = new Vote();
        vote.setPost(post);
        vote.setVoteType(voteDto.getVoteType());

        // vote.setUser(authService.getCurrentUser());

        vote.setUser(user);
        
        return vote;        
    }
    
}
