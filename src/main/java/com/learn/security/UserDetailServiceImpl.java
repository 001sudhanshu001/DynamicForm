package com.learn.security;

import com.learn.entity.AppUser;
import com.learn.repository.UserRepository;
import com.learn.security.dto.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User Not Found With UserName :: " + username)
        );
        return new UserDetailsImpl(appUser);
    }
}
