package nz.clem.authservice.service;

import nz.clem.authservice.model.User;
import nz.clem.authservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
