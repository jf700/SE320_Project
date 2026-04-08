package com.digitaltherapy.security;

import com.digitaltherapy.entity.User;
import com.digitaltherapy.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
        return new org.springframework.security.core.userdetails.User(
                user.getId().toString(), user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
