package com.revconnect.like;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import com.revconnect.like.controller.LikeController;
import com.revconnect.like.service.LikeService;
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

@WebMvcTest(LikeController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "vasanth")
public class LikeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LikeService likeService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void testLikePost() throws Exception {

        when(likeService.likePost(1L,"vasanth"))
                .thenReturn(Map.of("liked",true,"likeCount",1));

        mockMvc.perform(post("/posts/1/like")
                .with(user("vasanth")))
                .andExpect(status().isOk());
    }

    @Test
    void testUnlikePost() throws Exception {

        when(likeService.unlikePost(1L,"vasanth"))
                .thenReturn(Map.of("liked",false,"likeCount",0));

        mockMvc.perform(delete("/posts/1/like")
                .with(user("vasanth")))
                .andExpect(status().isOk());
    }
}
