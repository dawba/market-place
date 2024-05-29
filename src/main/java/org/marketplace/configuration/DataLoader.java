package org.marketplace.configuration;

import org.marketplace.models.User;
import org.marketplace.repositories.UserManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private UserManagementRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Environment env;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        if (userRepository.count() == 0) {
            User user = new User();
            user.setEmail(env.getProperty("user.default.email"));
            user.setPassword(passwordEncoder.encode(env.getProperty("user.default.password")));
            user.setLogin(env.getProperty("user.default.login"));
            userRepository.save(user);
        }
    }
}
