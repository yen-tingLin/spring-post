package com.example.post.dto;


public class SubpostDto {
    
    private Long id;
    private String subpostName;
    private String description;
    private Integer numberOfPosts;

    public SubpostDto() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubpostName() {
        return subpostName;
    }

    public void setSubpostName(String subpostName) {
        this.subpostName = subpostName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumberOfPosts() {
        return numberOfPosts;
    }

    public void setNumberOfPosts(Integer numberOfPosts) {
        this.numberOfPosts = numberOfPosts;
    }

}
