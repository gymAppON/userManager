package com.example.userManager.domain.auth;

import com.example.userManager.domain.auth.dto.LoginRequestDto;
import com.example.userManager.domain.auth.dto.SignupRequestDto;
import com.example.userManager.domain.user.dto.UserResponseDto;
import com.example.userManager.shared.exception.LogEnum;
import com.example.userManager.shared.exception.exceptions.general.CustomAlreadyExistException;
import com.example.userManager.infrastructure.security.jwt.JwtResponseDto;
import com.example.userManager.shared.exception.exceptions.general.CustomErrorResponse;
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

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = JwtResponseDto.class))}),
            @ApiResponse(responseCode = "4XX", description = "Login failed",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RuntimeException.class))})
    })
    public JwtResponseDto authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) throws Exception {
        String jwtToken = authService.login(loginRequestDto);
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
        UserResponseDto userDto = authService.register(signUpRequestDto);
        log.info("{}: User (id: {}) has accomplished registration process", LogEnum.CONTROLLER, userDto.id());
        return userDto;
    }

    @GetMapping(VERIF_URI+"/email"+VERIF_URI_CODE)
    @Operation(summary = "User account verification")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Email verification successful"),
            @ApiResponse(responseCode = "404", description = "User with this email not found",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CustomErrorResponse.class)) })
    })
    public void emailVerification(@PathVariable String verifCode) {
        UserResponseDto userResponseDto = authService.emailVerification(verifCode);
        log.info("{}: User (id: {}) has completed email verification", LogEnum.CONTROLLER, userResponseDto.id());
    }

    @GetMapping(VERIF_URI+"/password"+VERIF_URI_CODE)
    @Operation(summary = "User account verification")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Password verification successful"),
            @ApiResponse(responseCode = "404", description = "User with this password verification code not found",
                    content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CustomErrorResponse.class)) })
    })
    public void passwordVerification(@PathVariable String verifCode) {
        UserResponseDto userResponseDto = authService.passwordVerification(verifCode);
        log.info("{}: User (id: {}) has completed password verification", LogEnum.CONTROLLER, userResponseDto.id());
    }
}

