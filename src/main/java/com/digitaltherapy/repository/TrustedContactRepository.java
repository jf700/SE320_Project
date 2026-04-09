package com.digitaltherapy.repository;

import com.digitaltherapy.entity.TrustedContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrustedContactRepository extends JpaRepository<TrustedContact, UUID>{
    List<TrustedContact> findByUserId(UUID userId);
}
