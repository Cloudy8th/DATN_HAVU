package com.project.tmartweb.application.services.user;

import com.project.tmartweb.application.repositories.TokenRepository;
import com.project.tmartweb.application.repositories.UserRepository;
import com.project.tmartweb.application.responses.TokenResponse;
import com.project.tmartweb.application.services.file.FileService;
import com.project.tmartweb.application.services.role.RoleService;
import com.project.tmartweb.application.services.token.ITokenService;
import com.project.tmartweb.config.exceptions.ConflictException;
import com.project.tmartweb.config.exceptions.NotFoundException;
import com.project.tmartweb.config.security.CustomUserDetails;
import com.project.tmartweb.config.security.JwtTokenProvider;
import com.project.tmartweb.domain.dtos.*;
import com.project.tmartweb.domain.entities.Role;
import com.project.tmartweb.domain.entities.Token;
import com.project.tmartweb.domain.entities.User;
import com.project.tmartweb.domain.enums.RoleId;
import com.project.tmartweb.domain.paginate.BasePagination;
import com.project.tmartweb.domain.paginate.PaginationDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final RoleService roleService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ITokenService tokenService;
    private final TokenRepository tokenRepository;
    private final FileService fileService;

    @Override
    public TokenResponse Login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUserName();
        String password = userLoginDTO.getPassword();
        Optional<User> user = userRepository.findByUserName(username);
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            throw new NotFoundException("Tên đăng nhập hoặc mật khẩu không tồn tại", "User or password not found");
        }
        CustomUserDetails customUserDetails = new CustomUserDetails(user.get());
        UsernamePasswordAuthenticationToken userToken =
                new UsernamePasswordAuthenticationToken(
                        username, password, customUserDetails.getAuthorities()
                );
        authenticationManager.authenticate(userToken);
        String tokenString = jwtTokenProvider.generateToken(user.get());
//        Token token = new Token();
//        token.setUser(user.get());
//        token.setToken(tokenString);
//        token.setExpirationDate(jwtTokenProvider.getExpirationDate(tokenString));
//        token.setTokenType("Authentication");
//        tokenRepository.save(token);
        return new TokenResponse(tokenString, user.get().getId(), user.get().getFullName());
    }

    @Override
    public User getByUserName(String userName) {
        return userRepository.findByUserName(userName)
                             .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại", "User not found"));
    }

    @Override
    public User getByToken(String token) {
        Token tokenModel = tokenService.getById(token);
        if (tokenModel.getExpired()) {
            throw new NotFoundException("Token đã hết hạn", "Token expired");
        }
        return tokenModel.getUser();
    }

    @Override
    public String uploadImage(UUID id, MultipartFile file) {
        User user = getById(id);
        user.setImage(fileService.uploadFile(file));
        userRepository.save(user);
        return "Upload image successfully";
    }

    @Override
    public User editProfile(UUID id, UserEditProfileDTO userEditProfileDTO) {
        User user = getById(id);
        mapper.map(userEditProfileDTO, user);
        return userRepository.save(user);
    }

    @Override
    public String changePassword(UUID id, UserChangePassword userChangePassword) {
        User user = getById(id);
        if (!passwordEncoder.matches(userChangePassword.getOldPassword(), user.getPassword())) {
            throw new NotFoundException("Mật không tồn tại", "Old password is not correct");
        }
        user.setPassword(passwordEncoder.encode(userChangePassword.getNewPassword()));
        userRepository.save(user);
        return "Cập nhật mật khẩu thành công!";
    }

    @Override
    public PaginationDTO<User> getAllByFilter(String fullName,
                                              String userName,
                                              Date dateOfBirth,
                                              RoleId roleId,
                                              Integer page,
                                              Integer perPage) {
        Page<User> users = userRepository.
                findAllByFilter(fullName, userName, dateOfBirth,
                                roleId, PageRequest.of(page, perPage));
        BasePagination<User, UserRepository> pagination = new BasePagination<>();
        return pagination.paginate(page, perPage, users);
    }

    @Override
    public User insert(UserDTO userDTO) {
        Optional<String> phoneNumber = Optional.ofNullable(userDTO.getPhoneNumber());
        if (phoneNumber.isPresent()
                && userRepository.existsByPhoneNumber(phoneNumber.get())) {
            throw new ConflictException("Số điện thoại dã tồn tại", "Phone number is already exists");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ConflictException("Email đã tồn tại", "Email is already exists");
        }
        User user = mapper.map(userDTO, User.class);
        Role role = roleService.getById(userDTO.getRoleId());
        String encoderPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encoderPassword);
        user.setRole(role);
        return userRepository.save(user);
    }

    @Override
    public User update(UUID id, UserDTO userDTO) {
        User user = getById(id);
        Optional<String> phoneNumber = Optional.ofNullable(userDTO.getPhoneNumber());
        if (phoneNumber.isPresent()
                && user.getPhoneNumber() != null
                && !user.getPhoneNumber().equals(phoneNumber.get())
                && userRepository.existsByPhoneNumber(phoneNumber.get())) {
            throw new ConflictException("Số điện thoại dã tồn tại", "Phone number is already exists");
        }
        if (!user.getEmail().equals(userDTO.getEmail()) && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ConflictException("Email đã tồn tại", "Email is already exists");
        }
        Role role = roleService.getById(userDTO.getRoleId());
        user.setRole(role);
        UserUpdateDTO userUpdateDTO = mapper.map(userDTO, UserUpdateDTO.class);
        mapper.map(userDTO, userUpdateDTO);
        mapper.map(userUpdateDTO, user);
        return userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public PaginationDTO<User> getAll(Integer page, Integer perPage) {
        if (page == null && perPage == null) {
            return new PaginationDTO<>(userRepository
                                               .findAllByDeleted(false, Sort.by("createdAt")
                                                                            .descending()), null);
        }
        BasePagination<User, UserRepository> pagination = new BasePagination<>();
        Page<User> users = userRepository
                .findAllByDeleted(false, PageRequest.of(page, perPage, Sort.by("createdAt").descending()));
        return pagination.paginate(page, perPage, users);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public User getById(UUID id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Người dùng này không tồn tại", "User not found"));
    }
}
