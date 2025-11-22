package com.example.userManager.domain.user;

import com.example.userManager.domain.user.dto.UserRequestDto;
import com.example.userManager.domain.user.dto.UserResponseDto;
import com.example.userManager.shared.exception.LogEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private static final String URI_WITH_ID = "/{id}";
    private final UserService userService;
    private static final String OBJECT_NAME = "User";

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    @Operation(summary = "Create a new user", description = "Creates a new user")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "User created successfully",
//                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
//                            schema = @Schema(implementation = UserResponseDto.class))),
//            @ApiResponse(responseCode = "404", description = "User not found")
//    })
//    public UserResponseDto create(@RequestBody SignupRequestDto request) {
//        UserResponseDto userResponseDto = userService.create(request);
//        log.info("{}: {} created: {}", LogEnum.CONTROLLER,OBJECT_NAME, request);
//
//        return userResponseDto;
//    }

    @GetMapping(URI_WITH_ID)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user by ID", description = "Get a user by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found by id",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public UserResponseDto getById(@PathVariable UUID id) {
        UserResponseDto user = userService.getById(id);
        log.info("{}: Get {} by id: {}",LogEnum.CONTROLLER,OBJECT_NAME, id);
        return user;
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all users", description = "Retrieves a list of all users.")
    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserResponseDto.class)))
    public List<UserResponseDto> getAll() {
        List<UserResponseDto> users = userService.getAll();
        log.info("{}: Show all {}s",LogEnum.CONTROLLER,OBJECT_NAME);
        return users;
    }

    @PutMapping(URI_WITH_ID)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user details", description = "Updates the details of an existing user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public UserResponseDto update(@PathVariable UUID id, @RequestBody UserRequestDto request) {
        UserResponseDto updatedUser = userService.update(id, request);
        log.info("{}: Updated {} with id: {}", LogEnum.CONTROLLER,OBJECT_NAME, id);
        return updatedUser;
    }

    @DeleteMapping(URI_WITH_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a user", description = "Deletes a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
        log.info("{}: Deleted {} with id: {}",LogEnum.CONTROLLER,OBJECT_NAME, id);
    }
}