package com.faunadb.services;

import com.faunadb.model.CreateReplacePostData;
import com.faunadb.model.Post;
import com.faunadb.persistence.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public CompletableFuture<Post> createPost(CreateReplacePostData data) {
        CompletableFuture<Post> result =
            postRepository.nextId()
                .thenApply(id -> new Post(id, data.getTitle(), data.getTags()))
                .thenCompose(post -> postRepository.save(post));

        return result;
    }

    public CompletableFuture<List<Post>> createSeveralPosts(List<CreateReplacePostData> data) {
        CompletableFuture<List<Post>> result =
            postRepository.nextIds(data.size())
                .thenApply(ids ->
                    IntStream
                        .range(0, data.size())
                        .mapToObj(i -> new Post(ids.get(i), data.get(i).getTitle(), data.get(i).getTags()))
                        .collect(Collectors.toList()))
                .thenCompose(posts ->
                    postRepository.saveAll(posts));

        return result;
    }

    public CompletableFuture<Optional<Post>> retrievePost(String id) {
        return postRepository.find(id);
    }

    public CompletableFuture<List<Post>> retrievePosts() {
        return postRepository.findAll();
    }

    public CompletableFuture<List<Post>> retrievePostsByTitle(String title) {
        return postRepository.findByTitle(title);
    }

    public CompletableFuture<Optional<Post>> replacePost(String id, CreateReplacePostData data) {
        CompletableFuture<Optional<Post>> result =
            postRepository.find(id)
                .thenCompose(optionalPost ->
                    optionalPost
                        .map(post -> postRepository.save(new Post(id, data.getTitle(), data.getTags())).thenApply(Optional::of))
                        .orElseGet(() -> CompletableFuture.completedFuture(Optional.empty())));

        return result;
    }

    public CompletableFuture<Optional<Post>> deletePost(String id) {
        return postRepository.remove(id);
    }

}
