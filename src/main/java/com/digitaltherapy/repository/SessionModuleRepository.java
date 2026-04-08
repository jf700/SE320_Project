package com.digitaltherapy.repository;

import com.digitaltherapy.entity.SessionModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface SessionModuleRepository extends JpaRepository<SessionModule, UUID>{
}
