package com.project.tmartweb.application.repositories;

import com.project.tmartweb.domain.entities.Role;
import com.project.tmartweb.domain.enums.RoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, RoleId> {
}
