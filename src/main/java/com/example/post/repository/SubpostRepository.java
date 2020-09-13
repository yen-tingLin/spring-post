package com.example.post.repository;

import com.example.post.model.Subpost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubpostRepository extends JpaRepository<Subpost, Long> {
    
}
