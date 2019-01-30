package com.faunadb.persistence.common;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IdentityFactory {
    CompletableFuture<String> nextId();
    CompletableFuture<List<String>> nextIds(int size);
}
