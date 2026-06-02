package com.Neeloo.UserAuthService.repository;


import com.Neeloo.UserAuthService.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    /*
    JPA methods
     */

    Optional<Role> findByValue(String value);
}