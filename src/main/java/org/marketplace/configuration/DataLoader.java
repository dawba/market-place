package org.marketplace.configuration;

import org.marketplace.models.User;
import org.marketplace.repositories.UserManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private UserManagementRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (userRepository.count() == 0) {
            User user = new User();
            user.setEmail("user1@test.com");
            user.setPassword(passwordEncoder.encode("pass"));
            user.setLogin("user1");
            userRepository.save(user);
        }
    }
}
