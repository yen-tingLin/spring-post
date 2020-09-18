package com.example.post.model;

import java.util.Arrays;

import com.example.post.exception.SpringPostException;


public enum VoteType {
    LIKE(1), DISLIKE(-1);

    private int direction;

    VoteType(int direction) {}

    public static VoteType lookup(Integer direction) {
        return Arrays.stream(VoteType.values())
                .filter(value -> value.getDirection().equals(direction))
                .findAny()
                .orElseThrow(() -> 
                        new SpringPostException("Vote not found"));   
    }

    private Integer getDirection() {
        return direction;
    }

}
