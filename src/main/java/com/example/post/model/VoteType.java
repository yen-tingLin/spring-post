package com.example.post.model;



public enum VoteType {
    LIKE(1), DISLIKE(-1),
    ;

    VoteType(int direction) {}

}
