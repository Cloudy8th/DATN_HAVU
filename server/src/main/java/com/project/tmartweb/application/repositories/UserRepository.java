package com.project.tmartweb.application.repositories;

import com.project.tmartweb.domain.entities.User;
import com.project.tmartweb.domain.enums.RoleId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    @Query("SELECT u from User u WHERE u.userName = :userName or u.email = :userName or u.phoneNumber = :userName")
    Optional<User> findByUserName(String userName);

    List<User> findAllByDeleted(Boolean deleted, Sort sort);

    Page<User> findAllByDeleted(Boolean deleted, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (:fullName IS NULL OR :fullName = '' " +
            "OR u.fullName ILIKE concat(:fullName, ' %') " +
            "OR u.fullName ILIKE concat('% ', :fullName, ' %') " +
            "OR u.fullName ILIKE concat('% ', :fullName)) " +
            "AND (:userName IS NULL OR :userName = '' OR u.userName LIKE :userName " +
            "OR u.phoneNumber LIKE :userName OR u.email LIKE :userName) " +
            "AND (COALESCE(:dateOfBirth, u.dateOfBirth) = u.dateOfBirth) " +
            "AND (:roleId IS NULL OR :roleId = '' OR u.role.id = :roleId) " +
            "ORDER BY u.createdAt DESC")
    Page<User> findAllByFilter(String fullName,
                               String userName,
                               @Param("dateOfBirth") Date dateOfBirth,
                               RoleId roleId,
                               Pageable pageable);
}
