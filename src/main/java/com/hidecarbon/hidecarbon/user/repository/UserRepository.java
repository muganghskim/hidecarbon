package com.hidecarbon.hidecarbon.user.repository;



import com.hidecarbon.hidecarbon.user.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserEmail(String userEmail);

}
