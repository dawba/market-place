package org.marketplace.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.marketplace.models.Category;
import org.marketplace.requests.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CategoryManagementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void createCategory() throws Exception {
        String payload = "{\"name\":\"SampleCategory\"}";

        mockMvc.perform(post("/api/categories/add")
                        .contentType(APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    public void getCategoryById() throws Exception {
        // Create a category
        String createPayload = "{\"name\":\"SampleCategory\"}";
        mockMvc.perform(post("/api/categories/add")
                        .contentType(APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        // Fetch the created category
        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    public void getCategoryById_negative() throws Exception {
        // Fetch a category that does not exist
        mockMvc.perform(get("/api/categories/-1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    public void updateCategory() throws Exception {
        // Create a category
        String createPayload = "{\"name\":\"SampleCategory\"}";
        mockMvc.perform(post("/api/categories/add")
                        .contentType(APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        // Update the created category
        String updatePayload = "{\"id\":1, \"name\":\"UpdatedCategory\"}";
        MvcResult result = mockMvc.perform(put("/api/categories/update")
                        .contentType(APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        // Parse the response body into a Response object
        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Response<Category> response = mapper.readValue(responseBody, new TypeReference<Response<Category>>() {});

        // Assert that the name of the category in the response body is "UpdatedCategory"
        assertEquals("UpdatedCategory", response.getData().getName());
    }

    @Test
    public void deleteCategory_positive() throws Exception {
        // Create a category
        String createPayload = "{\"name\":\"SampleCategory\"}";
        mockMvc.perform(post("/api/categories/add")
                        .contentType(APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        // Delete the created category
        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    public void deleteCategory_negative() throws Exception {
        // Delete a category that does not exist
        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    public void getAllCategories_afterAddition() throws Exception {
        // Create a category
        String createPayload = "{\"name\":\"SampleCategory\"}";

        mockMvc.perform(post("/api/categories/add")
                        .contentType(APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        mockMvc.perform(post("/api/categories/add")
                        .contentType(APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        mockMvc.perform(post("/api/categories/add")
                        .contentType(APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));


        // Fetch all categories
        MvcResult result = mockMvc.perform(get("/api/categories/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Response<List<Category>> response = mapper.readValue(responseBody, new TypeReference<Response<List<Category>>>() {});

        // Assert that the number of categories in the response body is 3
        assertEquals(3, response.getData().size());
    }

    @Test
    public void getAllCategories_empty() throws Exception {
        // Fetch all categories
        MvcResult result = mockMvc.perform(get("/api/categories/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Response<List<Category>> response = mapper.readValue(responseBody, new TypeReference<Response<List<Category>>>() {});

        // Assert that the number of categories in the response body is 0
        assertEquals(0, response.getData().size());
    }
}