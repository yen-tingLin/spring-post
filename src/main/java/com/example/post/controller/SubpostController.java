package com.example.post.controller;

import java.util.List;

import com.example.post.dto.SubpostDto;
import com.example.post.service.SubpostService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/subpost")
@CrossOrigin(value = "http://localhost:4200")
public class SubpostController {

    private final SubpostService subpostService;

    @Autowired
    public SubpostController(SubpostService subpostService) {
        this.subpostService = subpostService;
    }

    @PostMapping("/create")
    public ResponseEntity<SubpostDto> createSubpost(@RequestBody SubpostDto subpostDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                        .body(subpostService.create(subpostDto));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<SubpostDto>> getAllSubposts() {
        return ResponseEntity.status(HttpStatus.OK)
                        .body(subpostService.getAllSubposts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubpostDto> getSingleSubpost(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                        .body(subpostService.getSingleSubpost(id));
    }
    
}
