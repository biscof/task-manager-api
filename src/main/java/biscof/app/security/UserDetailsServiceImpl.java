package biscof.app.security;

import biscof.app.exception.UserNotFoundException;
import biscof.app.model.User;
import biscof.app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No user found with username: " + email));

        return new CustomUserPrincipal(user);
    }
}
