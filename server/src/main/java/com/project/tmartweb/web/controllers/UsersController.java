package com.project.tmartweb.web.controllers;

import com.project.tmartweb.application.services.user.IUserService;
import com.project.tmartweb.config.helpers.GenerateValue;
import com.project.tmartweb.domain.dtos.UserChangePassword;
import com.project.tmartweb.domain.dtos.UserDTO;
import com.project.tmartweb.domain.dtos.UserEditProfileDTO;
import com.project.tmartweb.domain.dtos.UserLoginDTO;
import com.project.tmartweb.domain.entities.User;
import com.project.tmartweb.domain.enums.RoleId;
import com.project.tmartweb.web.base.RestAPI;
import com.project.tmartweb.web.base.RoleAdmin;
import com.project.tmartweb.web.base.RolesAdminUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestAPI("${api.prefix}/users")
@RequiredArgsConstructor
public class UsersController {
    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        userDTO.setUserName(GenerateValue.generateUsername());
        userDTO.setRoleId(RoleId.USER);
        var res = userService.insert(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("")
    @RoleAdmin
    public ResponseEntity<?> insertUser(@Valid @RequestBody UserDTO userDTO) {
        var res = userService.insert(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO) {
        var token = userService.Login(userLoginDTO);
        return ResponseEntity.status(200).body(token);
    }

    @GetMapping("")
    @RoleAdmin
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer perPage) {
        var result = userService.getAll(page, perPage);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/{id}")
    @RolesAdminUser
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        var result = userService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/token/{token}")
    @RolesAdminUser
    public ResponseEntity<?> getByToken(@PathVariable String token) {
        var result = userService.getByToken(token);
        return ResponseEntity.status(HttpStatus.OK)
                             .body(result);
    }

    @GetMapping("/username/{username}")
    @RolesAdminUser
    public ResponseEntity<?> getByUserName(@PathVariable String username) {
        var result = userService.getByUserName(username);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/{id}")
    @RoleAdmin
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        User user = userService.getById(id);
        userService.delete(user);
        return ResponseEntity.status(HttpStatus.OK).body("Delete successfully");
    }

    @PutMapping("/{id}")
    @RolesAdminUser
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody UserDTO userDTO, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                                               .stream()
                                               .map(FieldError::getDefaultMessage)
                                               .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(id, userDTO));
    }

    @PostMapping("/upload-image/{id}")
    @RolesAdminUser
    public ResponseEntity<?> uploadImage(@PathVariable UUID id, @RequestPart("image") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.uploadImage(id, file));
    }

    @PutMapping("/change-password/{id}")
    @RolesAdminUser
    public ResponseEntity<?> changePassword(@PathVariable UUID id,
                                            @Valid @RequestBody UserChangePassword userChangePassword) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.changePassword(id, userChangePassword));
    }

    @PutMapping("/edit-profile/{id}")
    @RolesAdminUser
    public ResponseEntity<?> editProfile(@PathVariable UUID id, @Valid @RequestBody UserEditProfileDTO userDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.editProfile(id, userDTO));
    }

    @GetMapping("/filter")
    @RoleAdmin
    public ResponseEntity<?> filterUsers(
            @RequestParam(name = "fullName", required = false) String fullName,
            @RequestParam(name = "userName", required = false) String userName,
            @RequestParam(name = "dateOfBirth", required = false) String dateOfBirth,
            @RequestParam(name = "roleId", required = false) RoleId roleId,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "perPage", required = false) Integer perPage
    ) {
        Date birthday = null;
        if (dateOfBirth != null) {
            birthday = new Date(Timestamp.valueOf(dateOfBirth).getTime());
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllByFilter(
                fullName, userName, birthday, roleId, page, perPage
        ));
    }
}
