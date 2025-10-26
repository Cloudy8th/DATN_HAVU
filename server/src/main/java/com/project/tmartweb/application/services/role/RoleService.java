package com.project.tmartweb.application.services.role;

import com.project.tmartweb.application.repositories.RoleRepository;
import com.project.tmartweb.config.exceptions.NotFoundException;
import com.project.tmartweb.domain.entities.Role;
import com.project.tmartweb.domain.enums.RoleId;
import com.project.tmartweb.domain.paginate.BasePagination;
import com.project.tmartweb.domain.paginate.PaginationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;

    @Override
    public PaginationDTO<Role> getAll(Integer page, Integer perPage) {
        if (page == null && perPage == null) {
            return new PaginationDTO<>(roleRepository.findAll(), null);
        }
        BasePagination<Role, RoleRepository> basePagination = new BasePagination<>(roleRepository);
        return basePagination.paginate(page, perPage);
    }

    @Override
    public Optional<Role> findById(RoleId id) {
        return roleRepository.findById(id);
    }

    @Override
    public Role getById(RoleId id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Vai trò không tồn tại!", "Role not found"));
    }
}
