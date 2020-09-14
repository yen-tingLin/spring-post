package com.example.post.service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.post.dto.SubpostDto;
import com.example.post.exception.SpringPostException;
import com.example.post.model.Subpost;
import com.example.post.repository.SubpostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubpostServiceImp implements SubpostService {

    private final SubpostRepository subpostRepository;

    @Autowired
    public SubpostServiceImp(SubpostRepository subpostRepository) {
        this.subpostRepository = subpostRepository;
    }


    @Transactional
    @Override
    public SubpostDto create(SubpostDto subpostDto) {
        
        Subpost newSubpost =  mapFromSubpostDto(subpostDto);
        Subpost savedSubpost = subpostRepository.save(newSubpost);
        // set id of subpostDto and return subpostDto back to the controller
        subpostDto.setId(savedSubpost.getSubpostId());

        return subpostDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<SubpostDto> getAllSubposts() {
        return subpostRepository.findAll()
                        .stream()
                        .map(this::mapToSubpostDto)
                        .collect(Collectors.toList());

    }

    @Override
    public SubpostDto getSingleSubpost(Long id) {
        Optional<Subpost> subpostOptional = subpostRepository.findById(id);
        subpostOptional.orElseThrow(() -> 
                new SpringPostException("Subpost not found with id " + id));
        
        Subpost subpostFound = subpostOptional.get();
        SubpostDto subpostDto = mapToSubpostDto(subpostFound);
        return subpostDto;
    }
         

    private Subpost mapFromSubpostDto(SubpostDto subpostDto) {
        Subpost subpost = new Subpost();
        subpost.setCategory(subpostDto.getSubpostName());
        subpost.setDescription(subpostDto.getDescription());
        return subpost;
    }

    public SubpostDto mapToSubpostDto(Subpost subpost) {
        SubpostDto subpostDto = new SubpostDto();
        subpostDto.setId(subpost.getSubpostId());
        subpostDto.setSubpostName(subpost.getCategory());
        subpostDto.setDescription(subpost.getDescription());
        subpostDto.setNumberOfPosts(subpost.getPostList().size());

        return subpostDto;

    }

 
}
