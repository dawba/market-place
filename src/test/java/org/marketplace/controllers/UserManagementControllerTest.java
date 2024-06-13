package org.marketplace.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.marketplace.configuration.DataLoader;
import org.marketplace.models.User;
import org.marketplace.requests.Response;
import org.marketplace.services.ResourceAccessAuthorizationService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.marketplace.enums.AccessStatus.ACCESS_GRANTED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
class UserManagementControllerTest {

    @MockBean
    ResourceAccessAuthorizationService resourceAccessAuthorizationService;
    MvcResult mvcResultAddUser;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JavaMailSender javaMailSender;
    @MockBean
    private DataLoader dataLoader;

    @BeforeEach
    void setUp() throws Exception {
        given(resourceAccessAuthorizationService.authorizeUserAccess(any(), anyLong())).willReturn(ACCESS_GRANTED);

        String payload = "{\"login\":\"user2\", " +
                "\"password\":\"password\", " +
                "\"role\":\"USER\", " +
                "\"email\":\"user2@gmail.com\", " +
                "\"phoneNumber\":\"123456789\"}";

        //create new User
        mvcResultAddUser = mockMvc.perform(post("/api/user/register")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        Long userId = TestUtil.extractUserIdFromMvcResult(mvcResultAddUser);
        assertNotNull(userId, "User ID should not be null");

    }

    @Test
    void addUser() throws Exception {
        String payload = "{\"login\":\"user3\", " +
                "\"password\":\"password\", " +
                "\"role\":\"USER\", " +
                "\"email\":\"user3@gmail.com\", " +
                "\"phoneNumber\":\"123456789\"}";

        //create new User
        MvcResult result = mockMvc.perform(post("/api/user/register")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        Long userId = TestUtil.extractUserIdFromMvcResult(result);
        assertNotNull(userId, "User ID should not be null");
    }

    @Test
    public void getAllUsers_afterAddition() throws Exception {
        // Create user payload
        String userPayload2 = "{\"login\":\"user3\", " +
                "\"password\":\"password\", " +
                "\"role\":\"USER\", " +
                "\"email\":\"user3@gmail.com\", " +
                "\"phoneNumber\":\"123456789\"}";
        String userPayload3 = "{\"login\":\"user4\", " +
                "\"password\":\"password\", " +
                "\"role\":\"USER\", " +
                "\"email\":\"user4@gmail.com\", " +
                "\"phoneNumber\":\"123456789\"}";

        // Create multiple users
        mockMvc.perform(post("/api/user/register")
                        .contentType(APPLICATION_JSON)
                        .content(userPayload2))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON));

        mockMvc.perform(post("/api/user/register")
                        .contentType(APPLICATION_JSON)
                        .content(userPayload3))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON));

        // Fetch all users
        MvcResult result = mockMvc.perform(get("/api/user/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Response<List<User>> response = mapper.readValue(responseBody, new TypeReference<Response<List<User>>>() {
        });

        // Assert that the number of users in the response body is 3
        assertEquals(3, response.getData().size());
    }

    @Test
    void getUserById() throws Exception {
        Long id = TestUtil.extractUserIdFromMvcResult(mvcResultAddUser);
        // Fetch the created user
        mockMvc.perform(get(String.format("/api/user/%d", id)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    void updateUser() throws Exception {
        Long id = TestUtil.extractUserIdFromMvcResult(mvcResultAddUser);
        String updatePayload = String.format(
                "{" +
                        "\"id\":%d, " +
                        "\"login\":\"user2\", " +
                        "\"password\":\"password\", " +
                        "\"role\":\"USER\", " +
                        "\"email\":\"user2@gmail.com\", " +
                        "\"phoneNumber\":\"111111111\"" +
                        "}", id);

        MvcResult result = mockMvc.perform(put("/api/user/update")
                        .contentType(APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        // Parse the response body into a Response object
        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Response<User> response = mapper.readValue(responseBody, new TypeReference<Response<User>>() {
        });

        assertEquals("111111111", response.getData().getPhoneNumber());
    }

    @Test
    void deleteUserById() throws Exception {
        Long id = TestUtil.extractUserIdFromMvcResult(mvcResultAddUser);

        mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/api/user/%d", id)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));
    }
}