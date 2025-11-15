package com.example.userManager.user;

import com.example.userManager.dto.request.UserRequestDto;
import com.example.userManager.dto.request.auth.SignupRequestDto;
import com.example.userManager.dto.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
//public class UserServiceImpl implements UserDetailsService, UserService {
public class UserServiceImpl implements UserService {
    @Override
    public UserResponseDto create(SignupRequestDto request) {
        return null;
    }

    @Override
    public UserResponseDto getById(UUID id) {
        return null;
    }

    @Override
    public List<UserResponseDto> getAll() {
        return List.of();
    }

    @Override
    public UserResponseDto update(UUID id, UserRequestDto request) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return null;
//    }
}
