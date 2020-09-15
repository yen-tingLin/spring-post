package com.example.post.dto;

public class PostResponse {
    
    private Long id;
    private String postTitle;
    private String url;
    private String description;
    private String userName;
    private String category;
    private Integer voteCount;
    private Integer commentCount;
    private String duration;
    private boolean likeVote;
    private boolean dislikeVote;

    public PostResponse() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isLikeVote() {
        return likeVote;
    }

    public void setLikeVote(boolean likeVote) {
        this.likeVote = likeVote;
    }

    public boolean isDislikeVote() {
        return dislikeVote;
    }

    public void setDislikeVote(boolean dislikeVote) {
        this.dislikeVote = dislikeVote;
    }

}
