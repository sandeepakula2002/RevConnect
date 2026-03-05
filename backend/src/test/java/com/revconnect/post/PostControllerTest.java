package com.revconnect.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revconnect.post.controller.PostController;
import com.revconnect.post.dto.PostDtos;
import com.revconnect.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(postController)
                .build();
    }

    @Test
    void testCreatePost() throws Exception {

        when(postService.createPost(any(), eq("demoUser")))
                .thenReturn(PostDtos.PostResponse.builder()
                        .id(1L)
                        .content("Hello")
                        .build());

        PostDtos.CreatePostRequest request =
                new PostDtos.CreatePostRequest();
        request.setContent("Hello");

        mockMvc.perform(post("/posts")
                        .principal(() -> "demoUser")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetPost() throws Exception {

        when(postService.getPost(1L, "demoUser"))
                .thenReturn(PostDtos.PostResponse.builder().id(1L).build());

        mockMvc.perform(get("/posts/1")
                        .principal(() -> "demoUser"))
                .andExpect(status().isOk());
    }
}