package com.revconnect.comment;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revconnect.comment.controller.CommentController;
import com.revconnect.comment.model.Comment;
import com.revconnect.comment.service.CommentService;
import com.revconnect.security.JwtAuthenticationFilter;
import com.revconnect.security.JwtTokenProvider;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "vasanth")
public class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CommentService commentService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testAddComment() throws Exception {

        Map<String,String> body = new HashMap<>();
        body.put("content","Nice post");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("Nice post");

        when(commentService.addComment(1L,"Nice post","vasanth"))
                .thenReturn(comment);

        mockMvc.perform(post("/posts/1/comments")
                .with(user("vasanth"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }
}
