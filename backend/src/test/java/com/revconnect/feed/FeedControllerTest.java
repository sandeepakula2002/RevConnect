package com.revconnect.feed;

import com.revconnect.feed.controller.FeedController;
import com.revconnect.feed.service.FeedService;
import com.revconnect.post.dto.PostDtos;
import com.revconnect.post.model.Post;
import com.revconnect.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FeedControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FeedService feedService;

    @InjectMocks
    private FeedController feedController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(feedController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void testGetFeed() throws Exception {

        // 🔥 Create valid user inside Post
        User author = new User();
        author.setId(2L);
        author.setUsername("author");

        Post post = new Post();
        post.setId(10L);
        post.setUser(author);
        post.setContent("Hello");

        Page<PostDtos.PostResponse> page =
                new PageImpl<>(new java.util.ArrayList<>());

        when(feedService.getFeed(anyString(), anyInt(), anyInt()))
                .thenReturn(page);

        mockMvc.perform(get("/feed")
                        .param("page","0")
                        .param("size","10")
                        .principal(() -> "demoUser"))
                .andExpect(status().isOk());
    }
}