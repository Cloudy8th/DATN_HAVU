package com.project.hmartweb.application.services.token;

import com.project.hmartweb.application.repositories.TokenRepository;
import com.project.hmartweb.config.exceptions.NotFoundException;
import com.project.hmartweb.domain.entities.Token;
import com.project.hmartweb.domain.paginate.PaginationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {
    private final TokenRepository tokenRepository;

    @Override
    public PaginationDTO<Token> getAll(Integer page, Integer perPage) {
        return null;
    }

    @Override
    public Optional<Token> findById(String token) {
        return tokenRepository.findById(token);
    }

    @Override
    public Token getById(String token) {
        return findById(token).orElseThrow(() -> new NotFoundException("Token không tồn tại!", "Token not found!"));
    }
}
