package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.auth.dto.RoleDto;
import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.common.auth.readers.RoleReader;
import com.prolegacy.atom2024backend.common.auth.readers.UserReader;
import com.prolegacy.atom2024backend.common.auth.services.UserService;
import com.prolegacy.atom2024backend.common.query.lazy.PageQuery;
import com.prolegacy.atom2024backend.common.query.lazy.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('admin')")
@CrossOrigin("*")
@RestController
@RequestMapping("administration/users")
@TypescriptEndpoint
public class UserAdminController {
    @Autowired
    private UserReader userReader;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleReader roleReader;

    @PostMapping()
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PutMapping("{userId}")
    public UserDto updateUser(@PathVariable UserId userId,
                              @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @PatchMapping("{userId}")
    public UserDto resetPassword(@PathVariable UserId userId) {
        return userService.resetPassword(userId);
    }

    @DeleteMapping("{userId}/archive")
    public UserDto archiveUser(@PathVariable UserId userId) {
        return userService.archiveUser(userId);
    }

    @PutMapping("{userId}/archive")
    public UserDto unarchiveUser(@PathVariable UserId userId) {
        return userService.unarchiveUser(userId);
    }

    @GetMapping("{userId}")
    public UserDto getUser(@PathVariable UserId userId,
                           @RequestParam(required = false, defaultValue = "false") Boolean joinRoles,
                           @RequestParam(required = false, defaultValue = "false") Boolean joinAvailableEndpoints) {
        return userReader.getUser(userId, joinRoles, joinAvailableEndpoints);
    }

    @GetMapping
    public List<UserDto> getUsers(
            @RequestParam(required = false, defaultValue = "false") Boolean joinRoles,
            @RequestParam(required = false, defaultValue = "false") Boolean joinAvailableStandEndpoints
    ) {
        return userReader.getUsers(joinRoles, joinAvailableStandEndpoints);
    }

    @PostMapping("search")
    public PageResponse<UserDto> searchUsers(@RequestBody PageQuery pageQuery) {
        return userReader.searchUsers(pageQuery);
    }

    @GetMapping("/roles")
    public List<RoleDto> getRoles() {
        return roleReader.getRoles();
    }
}
