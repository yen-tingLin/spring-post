package com.example.post.dto;

import com.example.post.model.VoteType;

public class VoteDto {

    private Long postId;
    private VoteType voteType;

    
    public VoteDto() {}


    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteType voteType) {
        this.voteType = voteType;
    }

}
