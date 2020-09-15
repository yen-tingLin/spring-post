package com.example.post.repository;

import java.util.Optional;

import com.example.post.model.Subpost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SubpostRepository extends JpaRepository<Subpost, Long> {

    Optional<Subpost> findByCategory(String category);
}
