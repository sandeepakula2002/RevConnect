package com.revconnect.backend.posts.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.revconnect.backend.posts.dto.CreatePostRequest;
import com.revconnect.backend.posts.dto.SharePostRequest;
import com.revconnect.backend.posts.model.Hashtag;
import com.revconnect.backend.posts.model.PostHashtag;
import com.revconnect.backend.posts.model.Posts;
import com.revconnect.backend.posts.model.Share;
import com.revconnect.backend.posts.repository.HashtagRepository;
import com.revconnect.backend.posts.repository.PostHashtagRepository;
import com.revconnect.backend.posts.repository.PostsRepository;
import com.revconnect.backend.posts.repository.ShareRepository;

@Service
public class PostsService {

    private final PostsRepository postsRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final ShareRepository shareRepository;

    //Constructor injection for PostsRepository
    public PostsService(PostsRepository postsRepository,
                    HashtagRepository hashtagRepository,
                    PostHashtagRepository postHashtagRepository,
                    ShareRepository shareRepository) {

    this.postsRepository = postsRepository;
    this.hashtagRepository = hashtagRepository;
    this.postHashtagRepository = postHashtagRepository;
    this.shareRepository = shareRepository;
}

    //Get all Posts 
    public List<Posts> getAllPosts() {
        return postsRepository.findAll();
    }

    // Get posts by user ID
    public List<Posts> getPostsByUserId(Long userId) {
        return postsRepository.findByUserId(userId);
    }
    
    // Create a new Post
    public Posts createPost(CreatePostRequest request) {

    Posts post = new Posts();

    post.setUserId(request.getUserId());
    post.setContent(request.getContent());

    post = postsRepository.save(post);

    if(request.getHashtags() != null){
        for(String tag : request.getHashtags()){
            Hashtag hashtag = hashtagRepository
                    .findByTag(tag)
                    .orElseGet(() -> {

                        Hashtag newTag = new Hashtag();
                        newTag.setTag(tag);

                        return hashtagRepository.save(newTag);
                    });

            PostHashtag postHashtag = new PostHashtag();

            postHashtag.setPostId(post.getId());
            postHashtag.setHashtagId(hashtag.getId());

            postHashtagRepository.save(postHashtag);
        }
    }
    return post;
}

    //Delete a Post by ID
    public void deletePost(Long postId) {
        postsRepository.deleteById(postId);
    }

    //update Post
    public Posts updatePost(Long id, Posts updatedPost) {

    Posts post = postsRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));

    post.setContent(updatedPost.getContent());

    return postsRepository.save(post);
    }

    
    //Share a Post
    public Share sharePost(SharePostRequest request){

    Share share = new Share();

    share.setUserId(request.getUserId());
    share.setPostId(request.getPostId());

    return shareRepository.save(share);

}
}
