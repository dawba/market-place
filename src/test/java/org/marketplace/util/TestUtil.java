package org.marketplace.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.marketplace.models.Advertisement;
import org.marketplace.models.User;
import org.marketplace.models.Category;
import org.marketplace.models.AdvertisementImage;
import org.marketplace.requests.Response;
import org.springframework.test.web.servlet.MvcResult;

public class TestUtil {

    // Method to extract Advertisement ID from MvcResult
    public static Long extractAdvertisementIdFromMvcResult(MvcResult result) {
        try {
            String responseBody = result.getResponse().getContentAsString();
            ObjectMapper mapper = JsonMapper.builder()
                    .findAndAddModules()
                    .build();
            Response<Advertisement> response = mapper.readValue(responseBody, new TypeReference<Response<Advertisement>>() {});
            return response.getData().getId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract Advertisement ID from MvcResult", e);
        }
    }

    // Method to extract User ID from MvcResult
    public static Long extractUserIdFromMvcResult(MvcResult result) {
        try {
            String responseBody = result.getResponse().getContentAsString();
            ObjectMapper mapper = new ObjectMapper();
            Response<User> response = mapper.readValue(responseBody, new TypeReference<Response<User>>() {});
            return response.getData().getId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract User ID from MvcResult", e);
        }
    }

    // Method to extract Category ID from MvcResult
    public static Long extractCategoryIdFromMvcResult(MvcResult result) {
        try {
            String responseBody = result.getResponse().getContentAsString();
            ObjectMapper mapper = new ObjectMapper();
            Response<Category> response = mapper.readValue(responseBody, new TypeReference<Response<Category>>() {});
            return response.getData().getId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract Category ID from MvcResult", e);
        }
    }

    // Method to extract AdvertisementImage ID from MvcResult
    public static Long extractAdvertisementImageIdFromMvcResult(MvcResult result) {
        try {
            String responseBody = result.getResponse().getContentAsString();
            ObjectMapper mapper = new ObjectMapper();
            Response<AdvertisementImage> response = mapper.readValue(responseBody, new TypeReference<Response<AdvertisementImage>>() {});
            return response.getData().getId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract AdvertisementImage ID from MvcResult", e);
        }
    }
}
