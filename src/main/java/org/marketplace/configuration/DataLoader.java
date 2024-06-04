package org.marketplace.configuration;

import org.marketplace.enums.AdvertisementStatus;
import org.marketplace.enums.UserRole;
import org.marketplace.models.Advertisement;
import org.marketplace.models.Category;
import org.marketplace.models.User;
import org.marketplace.repositories.AdvertisementManagementRepository;
import org.marketplace.repositories.CategoryManagementRepository;
import org.marketplace.repositories.UserManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Collections;

@Component
public class DataLoader implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private UserManagementRepository userRepository;

    @Autowired
    private AdvertisementManagementRepository advertisementRepository;

    @Autowired
    private CategoryManagementRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Environment env;


    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        if (userRepository.count() == 0) {
            createDefaultUser();
        }

        Category category;
        if(categoryRepository.count() == 0){
            category = createDefaultCategory();
        } else {
            category = categoryRepository.findAll().get(0);
        }

        if(advertisementRepository.count() == 0){
            createDefaultAdvertisement(category);
        }
    }

    private void createDefaultUser() {
        User user = new User();
        user.setEmail(env.getProperty("user.default.email"));
        user.setPassword(passwordEncoder.encode(env.getProperty("user.default.password")));
        user.setLogin(env.getProperty("user.default.login"));
        user.setVerified(true);
        user.setRole(UserRole.ADMIN);
        userRepository.save(user);
    }

    private void createDefaultAdvertisement(Category category) {
        Advertisement advertisement = new Advertisement();
        advertisement.setTitle("Sample Advertisement");
        advertisement.setDescription("This is a default advertisement");
        advertisement.setCategory(category);
        advertisement.setUser(userRepository.findByLogin(env.getProperty("user.default.login")));
        advertisement.setPrice(0.0);
        advertisement.setLocation("Default Location");
        advertisement.setStatus(AdvertisementStatus.ACTIVE);
        advertisement.setBuyerId(null);
        advertisement.setObservers(Collections.singletonList(userRepository.findByLogin(env.getProperty("user.default.login")).getEmail()));
        advertisementRepository.save(advertisement);
    }

    private Category createDefaultCategory() {
        Category category = new Category();
        category.setName("Category");
        categoryRepository.save(category);
        return category;
    }
}
