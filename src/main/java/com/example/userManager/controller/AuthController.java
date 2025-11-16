package com.example.userManager.controller;

import com.example.userManager.dto.request.auth.LoginRequestDto;
import com.example.userManager.dto.request.auth.SignupRequestDto;
import com.example.userManager.dto.response.UserResponseDto;
import com.example.userManager.exception.LogEnum;
import com.example.userManager.exception.exceptions.general.CustomAlreadyExistException;
import com.example.userManager.security.jwt.JwtResponseDto;
import com.example.userManager.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@SecurityRequirement(name = "")
@Slf4j
public class AuthController {

    private static final String VERIF_URI = "/verification";
    private static final String VERIF_URI_CODE = "/{verifCode}";

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Login user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = JwtResponseDto.class))}),
            @ApiResponse(responseCode = "4XX", description = "Login failed",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RuntimeException.class))})
    })
    public JwtResponseDto authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) throws Exception {
        String jwtToken = userService.login(loginRequestDto);
        //String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("{}: User (email: {}) has accomplished authentication process", LogEnum.CONTROLLER, loginRequestDto.email());
        return new JwtResponseDto(jwtToken);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registration successful",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponseDto.class))}),
            @ApiResponse(responseCode = "4XX", description = "Registration failed",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RuntimeException.class))})
    })
    public UserResponseDto registerUser(@Valid @RequestBody SignupRequestDto signUpRequestDto) throws CustomAlreadyExistException {
        UserResponseDto userDto = userService.create(signUpRequestDto);
        log.info("{}: User (id: {}) has accomplished registration process", LogEnum.CONTROLLER, userDto.id());
        return userDto;
    }
}

