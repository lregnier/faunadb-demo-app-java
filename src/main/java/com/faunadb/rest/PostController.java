package com.faunadb.rest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faunadb.model.CreateReplacePostData;
import com.faunadb.model.Post;
import com.faunadb.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "/posts")
    public CompletableFuture<ResponseEntity> createPost(HttpEntity<String> httpEntity) throws IOException {
        String requestBody = httpEntity.getBody();

        // Create single Post
        if(isCreateReplacePostData(requestBody)) {
            CreateReplacePostData data = unmarshalCreateReplacePostData(requestBody);
            CompletableFuture<ResponseEntity> result =
                postService.createPost(data)
                    .thenApply(post -> new ResponseEntity(post, HttpStatus.OK));
            return result;
        }

        // Create several Posts
        if(isCreateReplacePostDataCollection(requestBody)) {
            List<CreateReplacePostData> data = unmarshalCreateReplacePostDataCollection(requestBody);
            CompletableFuture<ResponseEntity> result =
                postService.createSeveralPosts(data)
                    .thenApply(post -> new ResponseEntity(post, HttpStatus.OK));
            return result;
        }

        return CompletableFuture.completedFuture(new ResponseEntity(HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/posts/{id}")
    public CompletableFuture<ResponseEntity> retrievePost(@PathVariable("id") String id) {
        CompletableFuture<ResponseEntity> result =
            postService.retrievePost(id)
                .thenApply(optionalPost ->
                    optionalPost
                        .map(post -> new ResponseEntity(post, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND))
            );
        return result;
    }

    @GetMapping("/posts")
    public CompletableFuture<List<Post>> retrievePosts() {
        CompletableFuture<List<Post>> result = postService.retrievePosts();
        return result;
    }

    @GetMapping(value = "/posts", params = {"title"})
    public CompletableFuture<List<Post>> retrievePostsByTitle(@RequestParam("title") String title) {
        CompletableFuture<List<Post>> result = postService.retrievePostsByTitle(title);
        return result;
    }

    @PutMapping(value = "/posts/{id}")
    public CompletableFuture<ResponseEntity> replacePost(@PathVariable("id") String id, @RequestBody CreateReplacePostData data) {
        CompletableFuture<ResponseEntity> result =
            postService.replacePost(id, data)
                .thenApply(optionalPost ->
                    optionalPost
                        .map(post -> new ResponseEntity(post, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND))
                );
        return result;
    }

    @DeleteMapping(value = "/posts/{id}")
    public CompletableFuture<ResponseEntity> deletePost(@PathVariable("id")String id) {
        CompletableFuture<ResponseEntity> result =
            postService.deletePost(id)
                .thenApply(optionalPost ->
                    optionalPost
                        .map(post -> new ResponseEntity(post, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND))
                    );
        return result;
    }

    private Boolean isCreateReplacePostData(String json) throws IOException {
        try {
            objectMapper.readValue(json, CreateReplacePostData.class);
            return true;
        } catch (JsonParseException | JsonMappingException e) {
            return false;
        }
    }

    private Boolean isCreateReplacePostDataCollection(String json) throws IOException {
        try {
            objectMapper.readValue(json, new TypeReference<List<CreateReplacePostData>>(){});
            return true;
        } catch (JsonParseException | JsonMappingException e) {
            return false;
        }
    }

    private CreateReplacePostData unmarshalCreateReplacePostData(String json) throws IOException {
        return objectMapper.readValue(json, CreateReplacePostData.class);
    }

    private List<CreateReplacePostData> unmarshalCreateReplacePostDataCollection(String json) throws IOException {
        return objectMapper.readValue(json, new TypeReference<List<CreateReplacePostData>>(){});
    }
}
