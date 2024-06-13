package org.marketplace.controllers;

import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.marketplace.configuration.DataLoader;
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

        Long userId = TestUtil.extractUserIdFromMvcResult(mvcResultUser);
        assertNotNull(userId, "User ID should not be null");

        MvcResult mvcResultCategory = mockMvc.perform(post("/api/categories/add")
                        .contentType(APPLICATION_JSON)
                        .content(categoryPayload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        Long categoryId = TestUtil.extractCategoryIdFromMvcResult(mvcResultCategory);
        assertNotNull(categoryId, "Category ID should not be null");

        String advertisementPayload = "{"
                + "\"title\": \"Used Bicycle\","
                + "\"description\": \"A well-maintained used bicycle in good condition. Suitable for daily commuting.\","
                + "\"category\": {"
                + "\"id\": " + categoryId + ","
                + "\"name\": \"Category\""
                + "},"
                + "\"user\": {"
                + "\"id\": " + 1 + ","
                + "\"login\": \"user2\","
                + "\"email\": \"user2@gmail.com\","
                + "\"phoneNumber\": \"123456789\","
                + "\"role\": \"USER\""
                + "},"
                + "\"price\": 150.0,"
                + "\"location\": \"New York\""
                + "}";
        MvcResult mvcResultAddAd = mockMvc.perform(post("/api/advertisement/add")
                        .contentType(APPLICATION_JSON)
                        .content(advertisementPayload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        Long advertisementId = TestUtil.extractAdvertisementIdFromMvcResult(mvcResultAddAd);
        assertNotNull(advertisementId, "Advertisement ID should not be null");
    }

    @After
    public void tearDown() {
    }
}
