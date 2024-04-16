package org.marketplace.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
    public void setUp() throws Exception {
        //this gets run before each test method
    }

    @After
    public void tearDown() throws Exception {
        //this gets run after each test method (mainly to clean things up)
    }

    @Test
    public void requestAddOfferTest() throws Exception {
        String payload = "{\"title\":\"title1\",\n" +
                "\"description\":\"desc1\",\n" +
                "\"price\":12,\n" +
                "\"location\":\"rkr\"\n" +
                "}";
        mockMvc.perform(post("/advertisements")
                        .contentType(APPLICATION_JSON).content(payload)
                )
                .andExpect(status().isBadRequest()).andExpect(content().string(
                        "{\"status\":\"BAD_REQUEST\",\"message\":\"Ads failed to be processed\",\"details\":\"Not implemented yet.\",\"path\":\"uri=/advertisements;client=127.0.0.1\"}"
                ));
    }
}