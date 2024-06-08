package org.marketplace.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.marketplace.configuration.DataLoader;
import org.marketplace.models.Advertisement;
import org.marketplace.models.User;
import org.marketplace.requests.Response;
import org.marketplace.util.TestUtil;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
public class AdvertisementManagementControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JavaMailSender javaMailSender;
    @MockBean
    private DataLoader dataLoader;

    private MvcResult mvcResultAddAd;

    private Long userId;

    private Long categoryId;

    @Test
    public void createAdvertisement() throws Exception {
        String userPayload = "{\"login\":\"user2\", " +
                "\"password\":\"password\", " +
                "\"role\":\"USER\", " +
                "\"email\":\"user2@gmail.com\", " +
                "\"phoneNumber\":\"123456789\"}";
        String categoryPayload = "{\"name\":\"Category\"}";
        //create new User
       MvcResult mvcResultUser = mockMvc.perform(post("/api/user/register")
                        .contentType(APPLICATION_JSON)
                        .content(userPayload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
               .andReturn();

        userId = TestUtil.extractUserIdFromMvcResult(mvcResultUser);
        assertNotNull(userId, "User ID should not be null");

        MvcResult mvcResultCategory = mockMvc.perform(post("/api/categories/add")
                        .contentType(APPLICATION_JSON)
                        .content(categoryPayload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        categoryId = TestUtil.extractCategoryIdFromMvcResult (mvcResultCategory);
        assertNotNull(categoryId, "Category ID should not be null");

        String advertisementPayload = "{"
                + "\"title\": \"Used Bicycle\","
                + "\"description\": \"A well-maintained used bicycle in good condition. Suitable for daily commuting.\","
                + "\"category\": {"
                + "\"id\": " + categoryId + ","
                + "\"name\": \"Category\""
                + "},"
                + "\"user\": {"
                + "\"id\": " + userId + ","
                + "\"login\": \"user2\","
                + "\"email\": \"user2@gmail.com\","
                + "\"phoneNumber\": \"123456789\","
                + "\"role\": \"USER\""
                + "},"
                + "\"price\": 150.0,"
                + "\"location\": \"New York\""
                + "}";
        mvcResultAddAd = mockMvc.perform(post("/api/advertisement/add")
                        .contentType(APPLICATION_JSON)
                        .content(advertisementPayload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();
        String responseBody = mvcResultAddAd.getResponse().getContentAsString();
        ObjectMapper mapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        Response<Advertisement> response = mapper.readValue(responseBody, new TypeReference<Response<Advertisement>>() {});

        Long advertisementId = TestUtil.extractAdvertisementIdFromMvcResult (mvcResultAddAd);
        assertNotNull(advertisementId, "Advertisement ID should not be null");
    }

    @After
    public void tearDown() {
    }
/*
    @Test
    public void testCreateAdvertisement_positive() throws Exception {
        //String payload = "{\"id\":1,\"title\":\"SampleAdvertisement\",\"description\":\"Thisisasampleadvertisement\",\"category\":{\"id\":1,\"name\":\"SampleCategory\"},\"user\":{\"id\":1,\"username\":\"SampleUser\"},\"price\":100.0,\"location\":\"SampleLocation\"}";
        String payload = "{"
                + "\"title\": \"Advertisement2\","
                + "\"description\": \"A well-maintained used bicycle in good condition. Suitable for daily commuting.\","
                + "\"category\": {"
                + "\"id\": " + categoryId + ","
                + "\"name\": \"Category\""
                + "},"
                + "\"user\": {"
                + "\"id\": " + userId + ","
                + "\"login\": \"user2\","
                + "\"email\": \"user2@gmail.com\","
                + "\"phoneNumber\": \"123456789\","
                + "\"role\": \"USER\""
                + "},"
                + "\"price\": 100.0,"
                + "\"location\": \"New York\""
                + "}";
        MvcResult mvcResult = mockMvc.perform(post("/api/advertisement/add")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        Long advertisementId = TestUtil.extractCategoryIdFromMvcResult (mvcResult);
        assertNotNull(advertisementId, "Advertisement ID should not be null");
    }*/
}
