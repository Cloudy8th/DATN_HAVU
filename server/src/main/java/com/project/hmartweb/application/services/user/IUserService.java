package com.project.hmartweb.application.services.user;

import com.project.hmartweb.application.responses.TokenResponse;
import com.project.hmartweb.application.services.base.IBaseService;
import com.project.hmartweb.domain.dtos.UserChangePassword;
import com.project.hmartweb.domain.dtos.UserDTO;
import com.project.hmartweb.domain.dtos.UserEditProfileDTO;
import com.project.hmartweb.domain.dtos.UserLoginDTO;
import com.project.hmartweb.domain.entities.User;
import com.project.hmartweb.domain.enums.RoleId;
import com.project.hmartweb.domain.paginate.PaginationDTO;
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
