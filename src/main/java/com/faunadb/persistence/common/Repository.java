package com.faunadb.persistence.common;

import com.faunadb.model.common.Entity;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Repository<T extends Entity> {
    CompletableFuture<T> save(T entity);
    CompletableFuture<List<T>> saveAll(List<T> entities);
    CompletableFuture<Optional<T>> remove(String id);
    CompletableFuture<Optional<T>> find(String id);
    CompletableFuture<List<T>> findAll();
}
