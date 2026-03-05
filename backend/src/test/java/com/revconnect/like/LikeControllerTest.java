package com.revconnect.like;

import com.revconnect.like.controller.LikeController;
import com.revconnect.like.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(likeController)
                .build();
    }

    @Test
    void testLikePost() throws Exception {

        when(likeService.likePost(1L, "demoUser"))
                .thenReturn(Map.of("liked", true, "likeCount", 5));

        mockMvc.perform(post("/posts/1/like")
                        .principal(() -> "demoUser"))
                .andExpect(status().isOk());
    }

    @Test
    void testUnlikePost() throws Exception {

        when(likeService.unlikePost(1L, "demoUser"))
                .thenReturn(Map.of("liked", false, "likeCount", 4));

        mockMvc.perform(delete("/posts/1/like")
                        .principal(() -> "demoUser"))
                .andExpect(status().isOk());
    }
}