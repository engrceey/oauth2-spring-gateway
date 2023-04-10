package com.zurum.oauthauthorisationserver.repositories;


import com.zurum.oauthauthorisationserver.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AuthUser, Long> {

    Optional<AuthUser> findByLogin(String login);
}
