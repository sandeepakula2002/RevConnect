package com.revconnect.backend.posts.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revconnect.backend.posts.dto.CreatePostRequest;
import com.revconnect.backend.posts.dto.SharePostRequest;
import com.revconnect.backend.posts.model.Posts;
import com.revconnect.backend.posts.model.Share;
import com.revconnect.backend.posts.service.PostsService;

@RestController
@RequestMapping("/posts")
public class PostsController {

    private final PostsService postsService;

    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    // Create Post
    @PostMapping
    public Posts createPost(@RequestBody CreatePostRequest request){
    return postsService.createPost(request);

}

     // Get All Posts
    @GetMapping()
    public List<Posts> getAllPosts() {

        return postsService.getAllPosts();
    }

     // Get User Posts
    @GetMapping("/user/{userId}")
    public List<Posts> getUserPosts(@PathVariable Long userId) {

        return postsService.getPostsByUserId(userId);

    }


    // Delete Post
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {

        postsService.deletePost(id);

    }

    //Update Post
    @PutMapping("/{id}")
    public Posts updatePost(@PathVariable Long id, @RequestBody Posts post){

    return postsService.updatePost(id, post);
}

    // Share Post
   @PostMapping("/share")
    public Share sharePost(@RequestBody SharePostRequest request){
    return postsService.sharePost(request);

}

}
