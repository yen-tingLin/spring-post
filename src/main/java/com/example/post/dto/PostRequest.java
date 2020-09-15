package com.example.post.dto;


public class PostRequest {

    private Long id;
    private String subpostName;
    private String title;
    private String url;
    private String description; 

    public PostRequest() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubpostName() {
        return subpostName;
    }

    public void setSubpostName(String subpostName) {
        this.subpostName = subpostName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
