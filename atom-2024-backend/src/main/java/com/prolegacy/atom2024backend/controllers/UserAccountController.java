package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.common.auth.dto.UserDto;
import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("{userId}/account")
@TypescriptEndpoint
public class UserAccountController {
    @Autowired
    private UserAccountService userAccountService;

    @PutMapping
    public UserDto updateUser(@PathVariable UserId userId, @RequestParam String password, @RequestBody UserDto userDto) {
        return userAccountService.updateUser(userId, password, userDto);
    }
}
