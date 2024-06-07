package org.marketplace.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.marketplace.configuration.DataLoader;
import org.marketplace.models.Category;
import org.marketplace.requests.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
public class AdvertisementImageManagementControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JavaMailSender javaMailSender;
    @MockBean
    private DataLoader dataLoader;

    @Before
    public void setUp() throws Exception {
        String payload = "{\"id\":1,\"title\":\"SampleAdvertisement\",\"description\":\"Thisisasampleadvertisement\",\"category\":{\"id\":1,\"name\":\"SampleCategory\"},\"user\":{\"id\":1,\"username\":\"SampleUser\"},\"price\":100.0,\"location\":\"SampleLocation\"}";

        mockMvc.perform(post("/api/advertisement/add")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void createAdvertisementImage_positive() throws Exception {
        String payload = "{\"id\":1,\"advertisement\":{\"id\":1},\"filepath\":\"filepath\"}";

        mockMvc.perform(post("/api/advertisement-images/add")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    public void testCreateAdvertisementImage_noAdvertisementId() throws Exception {
        String payload = "{\"id\":1,\"filepath\":\"filepath\"}";

        mockMvc.perform(post("/api/advertisement-images/add")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    public void createAdvertisementImage_invalidAdvertisementId() throws Exception {
        String payload = "{\"id\":1,\"advertisement\":{\"id\":-1},\"filepath\":\"filepath\"}";

        mockMvc.perform(post("/api/advertisement-images/add")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    public void updateAdvertisementImage() throws Exception {
        String payload = "{\"id\":1,\"advertisement\":{\"id\":1},\"filepath\":\"filepath\"}";

        mockMvc.perform(post("/api/advertisement-images/add")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        String updatePayload = "{\"id\":1,\"advertisement\":{\"id\":1},\"filepath\":\"updatedFilepath\"}";

        mockMvc.perform(put("/api/advertisement-images/update")
                        .contentType(APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    public void deleteAdvertisementImage() throws Exception {
        String payload = "{\"id\":1,\"advertisement\":{\"id\":1},\"filepath\":\"filepath\"}";

        mockMvc.perform(post("/api/advertisement-images/add")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        mockMvc.perform(delete("/api/advertisement-images/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));
    }
}