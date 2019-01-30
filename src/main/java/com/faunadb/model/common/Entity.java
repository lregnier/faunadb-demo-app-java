package com.faunadb.model.common;

public abstract class Entity {

    protected String id;

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

}
