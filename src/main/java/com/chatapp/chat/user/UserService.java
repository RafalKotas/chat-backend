package com.chatapp.chat.user;

import com.chatapp.chat.user.exception.EmailAlreadyUsedException;
import com.chatapp.chat.user.exception.UsernameAlreadyUsedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {

        log.info("Attempting to register user with email={} and username={}",
                user.getEmail(), user.getUsername());

        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Registration failed: email {} already in use", user.getEmail());
            throw new EmailAlreadyUsedException(user.getEmail());
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            log.warn("Registration failed: username {} already in use", user.getUsername());
            throw new UsernameAlreadyUsedException(user.getUsername());
        }

        log.debug("Saving new user to the database: email={} username={}",
                user.getEmail(), user.getUsername());

        User saved = userRepository.save(user);

        log.info("User successfully registered with id={} email={}",
                saved.getId(), saved.getEmail());

        return saved;
    }
}
