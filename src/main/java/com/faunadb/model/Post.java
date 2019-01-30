package com.faunadb.model;

import com.faunadb.client.types.FaunaConstructor;
import com.faunadb.client.types.FaunaField;
import com.faunadb.model.common.Entity;

import java.util.List;

public class Post extends Entity {

    private String title;
    private List<String> tags;

    @FaunaConstructor
    public Post(@FaunaField("id") String id, @FaunaField("title") String title, @FaunaField("tags") List<String> tags) {
        this.id = id;
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
