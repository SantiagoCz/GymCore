package com.santiagocz.membershipservice.repositories;

import com.santiagocz.membershipservice.domain.entities.Membership;
import com.santiagocz.membershipservice.domain.enums.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Optional<Membership> findByName(String name);

    List<Membership> findByStatus(MembershipStatus status);
}
