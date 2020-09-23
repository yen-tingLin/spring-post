package com.example.post.controller;

import com.example.post.dto.VoteDto;
import com.example.post.service.VoteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin(value = "http://localhost:4200")
public class VoteController {

    private final VoteService voteService;

    @Autowired
    VoteController(VoteService voteService) {
        this.voteService = voteService;
    }


    @PostMapping("/vote")
    public ResponseEntity<Void> vote(@RequestBody VoteDto voteDto) {
        voteService.vote(voteDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
}
