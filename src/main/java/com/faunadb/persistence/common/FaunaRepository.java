package com.faunadb.persistence.common;

import com.faunadb.client.FaunaClient;
import com.faunadb.client.errors.NotFoundException;
import com.faunadb.client.query.Expr;
import com.faunadb.client.types.Value;
import com.faunadb.model.common.Entity;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.Class;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.faunadb.client.query.Language.Class;
import static com.faunadb.client.query.Language.*;

public abstract class FaunaRepository<T extends Entity> implements Repository<T>, IdentityFactory {

    @Autowired
    protected FaunaClient client;

    protected final Class<T> entityType;
    protected final String className;
    protected final String classIndexName;

    public FaunaRepository(Class<T> entityType, String className, String classIndexName) {
        this.entityType = entityType;
        this.className = className;
        this.classIndexName = classIndexName;
    }

    @Override
    public CompletableFuture<String> nextId() {
        CompletableFuture<String> result =
            client.query(
                NewId()
            )
            .thenApply(value -> value.to(String.class).get());

        return result;
    }

    @Override
    public CompletableFuture<List<String>> nextIds(int size) {
        List<Integer> indexes = IntStream
            .range(0, size)
            .mapToObj(i -> i)
            .collect(Collectors.toList());


        CompletableFuture<List<String>> result =
            client.query(
                Map(
                    Value(indexes),
                    Lambda(Value("i"), NewId())
                )
            )
            .thenApply(value -> value.asCollectionOf(String.class).get().stream().collect(Collectors.toList()));

        return result;
    }

    @Override
    public CompletableFuture<T> save(T entity) {
        CompletableFuture<T> result =
            client.query(
                saveQuery(Value(entity.getId()), Value(entity))
            )
        .thenApply(this::toEntity);

        return result;
    }

    @Override
    public CompletableFuture<List<T>> saveAll(List<T> entities) {
        CompletableFuture<List<T>> result =
            client.query(
                Map(
                    Value(entities),
                    Lambda(
                        Value("entity"),
                        saveQuery(Select(Value("id"), Var("entity")), Var("entity"))
                    )
                )
            )
            .thenApply(this::toList);


        return result;
    }

    @Override
    public CompletableFuture<Optional<T>> remove(String id) {
        CompletableFuture<T> result =
            client.query(
                Select(
                    Value("data"),
                    Delete(Ref(Class(className), Value(id)))
                )
            )
            .thenApply(this::toEntity);

        CompletableFuture<Optional<T>> optionalResult = toOptionalResult(result);

        return optionalResult;
    }

    @Override
    public CompletableFuture<Optional<T>> find(String id) {
        CompletableFuture<T> result =
            client.query(
                Select(
                    Value("data"),
                    Get(Ref(Class(className), Value(id)))
                )
            )
            .thenApply(this::toEntity);

        CompletableFuture<Optional<T>> optionalResult = toOptionalResult(result);

        return optionalResult;
    }

    @Override
    public CompletableFuture<List<T>> findAll() {
        CompletableFuture<List<T>> result =
            client.query(
                SelectAll(
                    Value("data"),
                    Map(
                        Paginate(Match(Index(Value(classIndexName)))),
                        Lambda(Value("nextRef"), Select(Value("data"), Get(Var("nextRef"))))
                    )
                )
            )
            .thenApply(this::toList);

        return result;
    }

    protected Expr saveQuery(Expr id, Expr data) {
        Expr query =
            Select(
                Value("data"),
                If(
                    Exists(Ref(Class(className), id)),
                    Replace(Ref(Class(className), id), Obj("data", data)),
                    Create(Ref(Class(className), id), Obj("data", data))
                )
            );

        return query;
    }

    protected T toEntity(Value value) {
        return value.to(entityType).get();
    }

    protected List<T> toList(Value value) {
        return value.asCollectionOf(entityType).get().stream().collect(Collectors.toList());
    }

    protected CompletableFuture<Optional<T>> toOptionalResult(CompletableFuture<T> result) {
        CompletableFuture<Optional<T>> optionalResult =
            result.handle((v, t) -> {
                CompletableFuture<Optional<T>> r = new CompletableFuture<>();
                if(v != null) r.complete(Optional.of(v));
                else if(t != null && t.getCause() instanceof NotFoundException) r.complete(Optional.empty());
                else r.completeExceptionally(t);
                return r;
            }).thenCompose(Function.identity());

        return optionalResult;
    }

}
