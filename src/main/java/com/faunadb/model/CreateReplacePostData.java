package com.faunadb.model;

import java.util.List;

public class CreateReplacePostData {

    private String title;
    private List<String> tags;

    public CreateReplacePostData(String title, List<String> tags) {
        this.title = title;
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
