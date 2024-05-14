package org.marketplace.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdvertisementManagementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreateAdvertisement_positive() throws Exception {
        String payload = "{\"id\":1,\"title\":\"SampleAdvertisement\",\"description\":\"Thisisasampleadvertisement\",\"category\":{\"id\":1,\"name\":\"SampleCategory\"},\"user\":{\"id\":1,\"username\":\"SampleUser\"},\"price\":100.0,\"location\":\"SampleLocation\"}";

        mockMvc.perform(post("/api/advertisement/add")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON));
    }
}
