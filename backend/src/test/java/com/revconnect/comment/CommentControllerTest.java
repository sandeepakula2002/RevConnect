package com.revconnect.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revconnect.comment.controller.CommentController;
import com.revconnect.comment.dto.CommentResponse;
import com.revconnect.comment.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(commentController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void testAddComment() throws Exception {

        CommentResponse response = mock(CommentResponse.class);

        when(commentService.addComment(anyLong(), anyString(), anyString()))
                .thenReturn(response);

        mockMvc.perform(post("/posts/1/comments")
                        .principal(() -> "demoUser")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                Map.of("content", "Nice"))))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetComments() throws Exception {

        CommentResponse response = mock(CommentResponse.class);

        when(commentService.getComments(anyLong(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/posts/1/comments")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteComment() throws Exception {

        doNothing().when(commentService)
                .deleteComment(anyLong(), anyString());

        mockMvc.perform(delete("/comments/1")
                        .principal(() -> "demoUser"))
                .andExpect(status().isOk());
    }
}
