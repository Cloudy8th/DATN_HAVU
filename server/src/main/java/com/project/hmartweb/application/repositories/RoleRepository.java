package com.project.hmartweb.application.repositories;

import com.project.hmartweb.domain.entities.Role;
import com.project.hmartweb.domain.enums.RoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, RoleId> {
}
