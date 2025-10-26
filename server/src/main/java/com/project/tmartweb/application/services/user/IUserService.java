package com.project.tmartweb.application.services.user;

import com.project.tmartweb.application.responses.TokenResponse;
import com.project.tmartweb.application.services.base.IBaseService;
import com.project.tmartweb.domain.dtos.UserChangePassword;
import com.project.tmartweb.domain.dtos.UserDTO;
import com.project.tmartweb.domain.dtos.UserEditProfileDTO;
import com.project.tmartweb.domain.dtos.UserLoginDTO;
import com.project.tmartweb.domain.entities.User;
import com.project.tmartweb.domain.enums.RoleId;
import com.project.tmartweb.domain.paginate.PaginationDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

public interface IUserService extends IBaseService<User, UserDTO, UUID> {
    TokenResponse Login(UserLoginDTO userLoginDTO);

    User getByUserName(String userName);

    User getByToken(String token);

    String uploadImage(UUID userId, MultipartFile file);

    User editProfile(UUID id, UserEditProfileDTO userEditProfileDTO);

    String changePassword(UUID id, UserChangePassword userChangePassword);

    PaginationDTO<User> getAllByFilter(String fullName,
                                       String userName,
                                       Date dateOfBirth,
                                       RoleId roleId,
                                       Integer page,
                                       Integer perPage);
}
