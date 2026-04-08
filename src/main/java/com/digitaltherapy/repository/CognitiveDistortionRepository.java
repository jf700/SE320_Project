package com.digitaltherapy.repository;

import com.digitaltherapy.entity.CognitiveDistortion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface CognitiveDistortionRepository extends JpaRepository<CognitiveDistortion, String>{
}
