package com.hidecarbon.hidecarbon.user.repository;

import com.hidecarbon.hidecarbon.user.model.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {

    Optional<EmailAuth> findByEmail(String userEmail);
}

