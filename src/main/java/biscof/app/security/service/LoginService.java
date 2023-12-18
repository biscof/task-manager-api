package biscof.app.security.service;

import biscof.app.exception.AuthException;
import biscof.app.security.JwtUtils;
import biscof.app.security.LoginDto;
import biscof.app.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthenticationManager authManager;

    public String login(LoginDto loginDto) throws AuthenticationException {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new AuthException("Invalid username or password provided.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
        return jwtUtils.generateToken(userDetails);
    }
}
