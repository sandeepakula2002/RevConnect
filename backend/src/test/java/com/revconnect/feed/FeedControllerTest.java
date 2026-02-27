package com.revconnect.feed;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.revconnect.feed.controller.FeedController;
import com.revconnect.feed.service.FeedService;
import com.revconnect.post.dto.PostDtos;
import com.revconnect.security.JwtAuthenticationFilter;
import com.revconnect.security.JwtTokenProvider;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FeedController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "vasanth")
public class FeedControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    FeedService feedService;

    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void testGetFeed() throws Exception {

        Page<PostDtos.PostResponse> page =
                new PageImpl<>(List.of());

        when(feedService.getFeed("vasanth",0,10))
                .thenReturn(page);

        mockMvc.perform(get("/feed")
                .with(user("vasanth")))
                .andExpect(status().isOk());
    }
}
